// App Configuration and State
const API_BASE = 'http://localhost:8080/api';

const ROLES = {
    'user_1': { name: "Manoj", isProducer: false, lat: 28.6139, lon: 77.2090, city: "New Delhi" },
    'user_2': { name: "Ritika", isProducer: false, lat: 30.3165, lon: 78.0322, city: "Dehradun" },
    'prod_1': { name: "Himachal Mountain Farms", isProducer: true, id: "prod_1", lat: 31.1048, lon: 77.1734, city: "Shimla" },
    'prod_2': { name: "Ganga Valley Dairy", isProducer: true, id: "prod_2", lat: 30.0869, lon: 78.2676, city: "Rishikesh" }
};

let currentState = {
    isLoggedIn: false,
    currentRole: 'user_1',
    activeTab: 'market',
    selectedCategory: '',
    products: [],
    selectedReviewRating: 5
};

// Chart instances
let revenueChartInstance = null;
let productChartInstance = null;

// Document Ready
document.addEventListener('DOMContentLoaded', () => {
    // Keep app screen hidden and login screen visible initially
    document.getElementById('login-screen').classList.remove('d-none');
    document.getElementById('app-screen').classList.add('d-none');
});

// Perform login operation
function performLogin(type) {
    let selectedValue;
    if (type === 'consumer') {
        selectedValue = document.getElementById('login-consumer-select').value;
    } else {
        selectedValue = document.getElementById('login-producer-select').value;
    }

    currentState.currentRole = selectedValue;
    currentState.isLoggedIn = true;
    
    const roleInfo = ROLES[currentState.currentRole];

    // Update logged in user profile display
    document.getElementById('logged-user-name').innerText = roleInfo.name;

    // Show App, Hide Login screen
    document.getElementById('login-screen').classList.add('d-none');
    document.getElementById('app-screen').classList.remove('d-none');

    // Update location banner text
    const bannerText = document.getElementById('sim-banner-text');
    
    if (roleInfo.isProducer) {
        bannerText.innerHTML = `<i class="fa-solid fa-tractor text-amber"></i> Acting as Producer: <strong>${roleInfo.name}</strong>. Farm: <strong>${roleInfo.city}</strong> (${roleInfo.lat}° N, ${roleInfo.lon}° E)`;
        
        // Hide Consumer Portal link, show Producer Dashboard link
        document.getElementById('btn-nav-portal').classList.add('d-none');
        document.getElementById('btn-nav-producer').classList.remove('d-none');
        
        // Switch tab to producer dashboard
        switchTab('producer');
    } else {
        bannerText.innerHTML = `<i class="fa-solid fa-location-crosshairs text-emerald"></i> Logged in as Consumer: <strong>${roleInfo.name}</strong>. Location: <strong>${roleInfo.city}</strong> (${roleInfo.lat}° N, ${roleInfo.lon}° E)`;
        
        // Show Consumer Portal link, hide Producer Dashboard link
        document.getElementById('btn-nav-portal').classList.remove('d-none');
        document.getElementById('btn-nav-producer').classList.add('d-none');
        
        // Switch tab to marketplace
        switchTab('market');
    }
    
    showToast(`Logged in successfully as ${roleInfo.name}!`);
}

// Perform logout operation
function performLogout() {
    currentState.isLoggedIn = false;
    
    // Destroy charts
    if (revenueChartInstance) {
        revenueChartInstance.destroy();
        revenueChartInstance = null;
    }
    if (productChartInstance) {
        productChartInstance.destroy();
        productChartInstance = null;
    }

    // Reset view
    document.getElementById('app-screen').classList.add('d-none');
    document.getElementById('login-screen').classList.remove('d-none');
    
    showToast("Logged out successfully.");
}

// Switch tabs
function switchTab(tabName) {
    currentState.activeTab = tabName;
    
    // Manage active classes on navigation links
    document.querySelectorAll('.main-nav .nav-link').forEach(link => {
        link.classList.remove('active');
    });
    
    // Set active link
    const activeLink = document.getElementById(`btn-nav-${tabName}`);
    if (activeLink) activeLink.classList.add('active');

    // Hide all tabs
    document.querySelectorAll('.content-tab').forEach(tab => {
        tab.classList.add('d-none');
    });

    // Show selected tab
    const tabEl = document.getElementById(`tab-${tabName}`);
    if (tabEl) tabEl.classList.remove('d-none');

    // Trigger tab-specific loaders
    if (tabName === 'market') {
        fetchProducts();
    } else if (tabName === 'portal') {
        loadConsumerPortal();
    } else if (tabName === 'producer') {
        loadProducerDashboard();
    }
}

// Update filter distance label
function updateRangeLabel(val) {
    document.getElementById('range-val-display').innerText = val + " km";
}

// Filter products by category
function filterCategory(btn, category) {
    // Remove active state from category buttons
    document.querySelectorAll('#category-bar .cat-btn').forEach(b => b.classList.remove('active'));
    // Set active
    btn.classList.add('active');
    
    currentState.selectedCategory = category;
    fetchProducts();
}

// Fetch products from backend
async function fetchProducts() {
    const searchVal = document.getElementById('search-input').value;
    const maxDist = document.getElementById('geo-range').value;
    
    const roleInfo = ROLES[currentState.currentRole];
    
    let url = `${API_BASE}/products?`;
    if (currentState.selectedCategory) {
        url += `category=${encodeURIComponent(currentState.selectedCategory)}&`;
    }
    if (searchVal) {
        url += `search=${encodeURIComponent(searchVal)}&`;
    }
    
    // If user is a consumer, filter by their distance
    if (!roleInfo.isProducer) {
        url += `latitude=${roleInfo.lat}&longitude=${roleInfo.lon}&maxDistance=${maxDist}`;
    }

    try {
        const response = await fetch(url);
        const wrappedProducts = await response.json();
        
        // Wrapped product structure: { product: {...}, distance: X }
        currentState.products = wrappedProducts;
        renderProducts(wrappedProducts);
    } catch (err) {
        console.error("Error fetching products:", err);
        showToast("Error fetching products from server", true);
    }
}

// Render products in marketplace grid
function renderProducts(wrappedProducts) {
    const listEl = document.getElementById('product-list');
    listEl.innerHTML = '';

    if (wrappedProducts.length === 0) {
        listEl.innerHTML = `
            <div class="full-width text-center" style="grid-column: 1/-1; padding: 3rem;">
                <i class="fa-solid fa-basket-shopping" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
                <p style="color: var(--text-secondary); font-weight: 500;">No products found matching your search or distance criteria.</p>
            </div>
        `;
        return;
    }

    wrappedProducts.forEach(wrapper => {
        const product = wrapper.product;
        const dist = wrapper.distance;
        
        const distText = dist >= 0 ? `${dist.toFixed(1)} km away` : 'Distance unknown';
        
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
            <div class="product-img-wrapper">
                <img class="product-img" src="${product.imageUrl}" alt="${product.name}">
                ${dist >= 0 ? `<span class="distance-badge"><i class="fa-solid fa-map-pin"></i> ${distText}</span>` : ''}
                <span class="category-badge">${product.category}</span>
            </div>
            <div class="product-body">
                <div class="product-producer">${product.producerName}</div>
                <h3 class="product-title">${product.name}</h3>
                <div class="product-origin"><i class="fa-solid fa-mountain-sun"></i> ${product.origin}</div>
                <div class="product-meta">
                    <div class="product-price-box">
                        <span class="product-price">₹${product.price}</span>
                        <span class="product-unit">per ${product.unit}</span>
                    </div>
                    <button class="btn btn-outline" onclick="openProductModal('${product.id}')">Details</button>
                </div>
            </div>
        `;
        listEl.appendChild(card);
    });
}

// Open Product Detail Modal
let activePurchaseTab = 'once';

async function openProductModal(productId) {
    const modal = document.getElementById('product-detail-modal');
    const bodyEl = document.getElementById('modal-product-detail-body');
    
    // Find product in state
    const productWrapper = currentState.products.find(p => p.product.id === productId);
    if (!productWrapper) return;
    
    const product = productWrapper.product;
    currentState.activeModalProduct = product;
    activePurchaseTab = 'once';
    
    modal.classList.remove('d-none');
    bodyEl.innerHTML = `<div class="full-width text-center" style="grid-column: 1/-1;"><i class="fa-solid fa-spinner fa-spin" style="font-size: 2rem; color: var(--primary);"></i></div>`;
    
    // Fetch product reviews
    let reviews = [];
    try {
        const res = await fetch(`${API_BASE}/products/${productId}/reviews`);
        reviews = await res.json();
    } catch (e) {
        console.error("Error fetching reviews", e);
    }
    
    const isProducerRole = ROLES[currentState.currentRole].isProducer;

    bodyEl.innerHTML = `
        <div class="modal-image-col">
            <img src="${product.imageUrl}" alt="${product.name}">
            <div class="modal-producer-badge">
                <h4>Listed by ${product.producerName}</h4>
                <p><i class="fa-solid fa-map-location-dot"></i> ${product.origin}</p>
                <p><i class="fa-solid fa-industry"></i> Ingredients: ${product.ingredients.join(', ')}</p>
            </div>
        </div>
        <div class="modal-details-col">
            <span class="tag-badge bg-emerald" style="display:inline-block; margin-bottom:0.5rem;">${product.category}</span>
            <h1 class="product-title" style="font-size: 1.8rem; margin-bottom: 0.5rem;">${product.name}</h1>
            <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1rem;">${product.description}</p>
            
            <div class="product-price-box" style="margin-bottom: 1.25rem;">
                <span class="product-price" style="font-size: 1.5rem;">₹${product.price}</span>
                <span class="product-unit">per ${product.unit} (Stock: ${product.stock})</span>
            </div>

            ${!isProducerRole ? `
                <div class="action-box">
                    <div class="purchase-tabs">
                        <button class="p-tab-btn active" id="tab-purchase-once" onclick="switchPurchaseTab('once')">Buy Once</button>
                        <button class="p-tab-btn" id="tab-purchase-sub" onclick="switchPurchaseTab('sub')">Subscribe & Save</button>
                    </div>

                    <div class="qty-input-box">
                        <label for="order-quantity">Quantity:</label>
                        <input type="number" id="order-quantity" value="1" min="1" max="${product.stock}">
                    </div>

                    <!-- Subscribe details -->
                    <div id="sub-frequency-group" class="form-group d-none" style="margin-bottom: 1rem;">
                        <label for="sub-frequency" style="font-size: 0.75rem; font-weight:700;">Delivery Schedule</label>
                        <select id="sub-frequency" style="padding: 0.4rem; font-size: 0.85rem;">
                            <option value="WEEKLY">Every Week (Weekly)</option>
                            <option value="BIWEEKLY">Every 2 Weeks (Bi-weekly)</option>
                            <option value="MONTHLY">Every Month (Monthly)</option>
                        </select>
                    </div>

                    <button class="btn btn-primary" style="width: 100%; justify-content: center;" onclick="processPurchase()">
                        <i class="fa-solid fa-cart-shopping"></i> <span id="purchase-btn-text">Confirm Order</span>
                    </button>
                </div>
            ` : `
                <div class="action-box text-center" style="color: var(--text-secondary); font-size: 0.85rem;">
                    <i class="fa-solid fa-lock"></i> Purchases are disabled when browsing as a producer. Switch roles in the top-right to buy.
                </div>
            `}

            <!-- Reviews List Section -->
            <div class="reviews-section">
                <h3>Customer Reviews</h3>
                <div id="reviews-list">
                    ${reviews.length === 0 ? '<p style="font-size:0.8rem; color:var(--text-muted);">No reviews yet. Be the first to share your experience!</p>' : ''}
                    ${reviews.map(r => `
                        <div class="review-item">
                            <div class="review-header">
                                <span class="review-user">${r.userName}</span>
                                <span class="review-date">${new Date(r.createdAt).toLocaleDateString()}</span>
                            </div>
                            <div class="review-rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</div>
                            <p class="review-comment">${r.comment}</p>
                        </div>
                    `).join('')}
                </div>

                <!-- Add Review form -->
                ${!isProducerRole ? `
                    <div class="write-review-form">
                        <h4>Write a Review</h4>
                        <div class="rating-select">
                            <i class="fa-solid fa-star star-input active" data-rating="1" onclick="setReviewRating(1)"></i>
                            <i class="fa-solid fa-star star-input active" data-rating="2" onclick="setReviewRating(2)"></i>
                            <i class="fa-solid fa-star star-input active" data-rating="3" onclick="setReviewRating(3)"></i>
                            <i class="fa-solid fa-star star-input active" data-rating="4" onclick="setReviewRating(4)"></i>
                            <i class="fa-solid fa-star star-input active" data-rating="5" onclick="setReviewRating(5)"></i>
                        </div>
                        <textarea id="review-comment" class="review-textarea" rows="2" placeholder="Tell other food enthusiasts how it tasted..."></textarea>
                        <button class="btn btn-secondary" style="padding: 0.4rem 1rem; font-size: 0.8rem;" onclick="submitReview()">Submit Review</button>
                    </div>
                ` : ''}
            </div>
        </div>
    `;
    
    // Reset review rating
    currentState.selectedReviewRating = 5;
}

function closeProductModal() {
    document.getElementById('product-detail-modal').classList.add('d-none');
}

// Toggle purchase tabs in modal
function switchPurchaseTab(type) {
    activePurchaseTab = type;
    
    const onceBtn = document.getElementById('tab-purchase-once');
    const subBtn = document.getElementById('tab-purchase-sub');
    const subGroup = document.getElementById('sub-frequency-group');
    const btnText = document.getElementById('purchase-btn-text');

    if (type === 'once') {
        onceBtn.classList.add('active');
        subBtn.classList.remove('active');
        subGroup.classList.add('d-none');
        btnText.innerText = "Confirm Order";
    } else {
        onceBtn.classList.remove('active');
        subBtn.classList.add('active');
        subGroup.classList.remove('d-none');
        btnText.innerText = "Subscribe Now";
    }
}

// Handle star ratings in form
function setReviewRating(rating) {
    currentState.selectedReviewRating = rating;
    document.querySelectorAll('.rating-select .star-input').forEach(star => {
        const val = parseInt(star.getAttribute('data-rating'));
        if (val <= rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

// Submit a customer review
async function submitReview() {
    const product = currentState.activeModalProduct;
    const comment = document.getElementById('review-comment').value;
    const rating = currentState.selectedReviewRating;
    const userVal = ROLES[currentState.currentRole];

    if (!comment.trim()) {
        showToast("Please enter a review comment", true);
        return;
    }

    const reviewData = {
        userId: currentState.currentRole,
        userName: userVal.name,
        rating: rating,
        comment: comment
    };

    try {
        const res = await fetch(`${API_BASE}/products/${product.id}/reviews`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reviewData)
        });
        
        if (res.ok) {
            showToast("Review submitted successfully!");
            // Reopen modal to reload reviews
            openProductModal(product.id);
        } else {
            showToast("Failed to submit review", true);
        }
    } catch (e) {
        console.error(e);
        showToast("Network error submitting review", true);
    }
}

// Buy or subscribe process
async function processPurchase() {
    const product = currentState.activeModalProduct;
    const qty = parseInt(document.getElementById('order-quantity').value);
    const userVal = ROLES[currentState.currentRole];

    if (isNaN(qty) || qty <= 0) {
        showToast("Invalid quantity selected", true);
        return;
    }

    if (qty > product.stock) {
        showToast(`Only ${product.stock} items left in stock`, true);
        return;
    }

    if (activePurchaseTab === 'once') {
        // Place one-time order
        const orderData = {
            userId: currentState.currentRole,
            userName: userVal.name,
            productId: product.id,
            productName: product.name,
            producerId: product.producerId,
            price: product.price,
            quantity: qty
        };

        try {
            const res = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData)
            });

            if (res.ok) {
                showToast("Order placed successfully! Check Consumer Portal.");
                closeProductModal();
                fetchProducts(); // Refresh stock in list
            } else {
                showToast("Order checkout failed", true);
            }
        } catch (e) {
            console.error(e);
            showToast("Network error ordering item", true);
        }
    } else {
        // Place recurring subscription
        const freq = document.getElementById('sub-frequency').value;
        const subData = {
            userId: currentState.currentRole,
            productId: product.id,
            productName: product.name,
            producerName: product.producerName,
            frequency: freq,
            quantity: qty
        };

        try {
            const res = await fetch(`${API_BASE}/subscriptions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(subData)
            });

            if (res.ok) {
                showToast("Subscription scheduled successfully!");
                closeProductModal();
            } else {
                showToast("Subscription setup failed", true);
            }
        } catch (e) {
            console.error(e);
            showToast("Network error creating subscription", true);
        }
    }
}

// 2. CONSUMER PORTAL TAB LOADS
async function loadConsumerPortal() {
    const userId = currentState.currentRole;
    
    // Load subscriptions
    const subList = document.getElementById('subscription-list');
    subList.innerHTML = `<div class="text-center"><i class="fa-solid fa-spinner fa-spin" style="font-size:1.5rem; color:var(--primary);"></i></div>`;
    
    try {
        const subRes = await fetch(`${API_BASE}/subscriptions?userId=${userId}`);
        const subs = await subRes.json();
        
        subList.innerHTML = '';
        if (subs.length === 0) {
            subList.innerHTML = '<p style="color:var(--text-muted); font-size:0.9rem;">You do not have any active subscriptions. Set some up on the marketplace!</p>';
        } else {
            subs.forEach(sub => {
                const isActive = sub.status === 'ACTIVE';
                const card = document.createElement('div');
                card.className = 'sub-card';
                card.innerHTML = `
                    <div class="sub-info">
                        <h3>${sub.productName}</h3>
                        <p>Producer: <strong>${sub.producerName}</strong></p>
                        <p>Quantity: <strong>${sub.quantity} units</strong></p>
                        <p>Next Delivery: <strong>${new Date(sub.nextDeliveryDate).toLocaleDateString()}</strong></p>
                        <span class="sub-frequency-badge">${sub.frequency}</span>
                    </div>
                    <div class="sub-actions">
                        <span class="sub-status">
                            <span class="status-dot ${isActive ? 'active' : 'paused'}"></span>
                            ${isActive ? 'Active' : 'Paused'}
                        </span>
                        <div style="display:flex; gap:0.5rem; margin-top:0.5rem;">
                            <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.75rem;" 
                                onclick="toggleSubscription('${sub.id}', '${isActive ? 'PAUSED' : 'ACTIVE'}')">
                                ${isActive ? 'Pause' : 'Resume'}
                            </button>
                            <button class="btn btn-danger" style="padding:0.4rem 0.8rem; font-size:0.75rem;" 
                                onclick="cancelSubscription('${sub.id}')">Cancel</button>
                        </div>
                    </div>
                `;
                subList.appendChild(card);
            });
        }
    } catch (e) {
        console.error(e);
        subList.innerHTML = '<p class="text-danger">Failed to load subscriptions.</p>';
    }

    // Load order history
    const historyList = document.getElementById('order-history-list');
    historyList.innerHTML = `<div class="text-center"><i class="fa-solid fa-spinner fa-spin" style="font-size:1.5rem; color:var(--primary);"></i></div>`;
    
    try {
        const orderRes = await fetch(`${API_BASE}/orders?userId=${userId}`);
        const orders = await orderRes.json();
        
        historyList.innerHTML = '';
        if (orders.length === 0) {
            historyList.innerHTML = '<p style="color:var(--text-muted); font-size:0.9rem;">No orders placed yet.</p>';
        } else {
            orders.sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt)).forEach(order => {
                const card = document.createElement('div');
                card.className = 'order-card';
                card.innerHTML = `
                    <div class="order-info">
                        <h3>${order.productName}</h3>
                        <p class="order-date"><i class="fa-solid fa-calendar-day"></i> ${new Date(order.createdAt).toLocaleString()}</p>
                        <p class="order-qty">Qty: ${order.quantity}</p>
                    </div>
                    <div class="order-total">
                        ₹${order.totalAmount.toFixed(2)}
                    </div>
                `;
                historyList.appendChild(card);
            });
        }
    } catch (e) {
        console.error(e);
        historyList.innerHTML = '<p class="text-danger">Failed to load order history.</p>';
    }
}

// Pause/resume subscription
async function toggleSubscription(subId, newStatus) {
    try {
        const res = await fetch(`${API_BASE}/subscriptions/${subId}/status?status=${newStatus}`, {
            method: 'PUT'
        });
        if (res.ok) {
            showToast(`Subscription ${newStatus === 'ACTIVE' ? 'resumed' : 'paused'} successfully!`);
            loadConsumerPortal();
        } else {
            showToast("Failed to update status", true);
        }
    } catch (e) {
        console.error(e);
        showToast("Network error updating status", true);
    }
}

// Cancel subscription
async function cancelSubscription(subId) {
    if (!confirm("Are you sure you want to cancel this delivery subscription?")) return;
    try {
        const res = await fetch(`${API_BASE}/subscriptions/${subId}`, {
            method: 'DELETE'
        });
        if (res.ok) {
            showToast("Subscription cancelled.");
            loadConsumerPortal();
        } else {
            showToast("Failed to cancel subscription", true);
        }
    } catch (e) {
        console.error(e);
        showToast("Network error cancelling subscription", true);
    }
}

// 3. PRODUCER DASHBOARD TAB LOADS
async function loadProducerDashboard() {
    const producerId = currentState.currentRole;
    
    // Fetch analytics metrics
    try {
        const res = await fetch(`${API_BASE}/analytics/producer/${producerId}`);
        const analytics = await res.json();
        
        // Update stats
        document.getElementById('stat-revenue').innerText = `₹${analytics.totalRevenue.toFixed(2)}`;
        document.getElementById('stat-orders').innerText = analytics.totalOrdersCount;
        document.getElementById('stat-subscriptions').innerText = analytics.activeSubscriptionsCount;

        // Render charts
        renderProducerCharts(analytics);
    } catch (e) {
        console.error(e);
        showToast("Failed to fetch analytics metrics", true);
    }

    // Load active listings for the producer
    loadProducerListings(producerId);
}

// Load producer listings
async function loadProducerListings(producerId) {
    const gridEl = document.getElementById('producer-product-list');
    gridEl.innerHTML = `<div class="full-width text-center" style="grid-column: 1/-1;"><i class="fa-solid fa-spinner fa-spin" style="font-size:1.5rem; color:var(--primary);"></i></div>`;
    
    try {
        const res = await fetch(`${API_BASE}/products/producer/${producerId}`);
        const listings = await res.json();
        
        gridEl.innerHTML = '';
        if (listings.length === 0) {
            gridEl.innerHTML = `<p style="color:var(--text-muted); font-size:0.9rem; grid-column:1/-1;">You have no active listings. Create one to start selling!</p>`;
        } else {
            listings.forEach(item => {
                const card = document.createElement('div');
                card.className = 'producer-prod-card';
                card.innerHTML = `
                    <div>
                        <h4>${item.name}</h4>
                        <p>Category: <strong>${item.category}</strong></p>
                        <p>Origin: <strong>${item.origin}</strong></p>
                        <div class="producer-prod-price">₹${item.price}</div>
                    </div>
                    <div class="producer-prod-footer">
                        <span>Stock: <strong>${item.stock}</strong></span>
                        <button class="btn btn-danger" style="padding:0.3rem 0.6rem; font-size:0.7rem;" onclick="deleteListing('${item.id}')">
                            <i class="fa-solid fa-trash-can"></i> Delete
                        </button>
                    </div>
                `;
                gridEl.appendChild(card);
            });
        }
    } catch (e) {
        console.error(e);
        gridEl.innerHTML = `<p class="text-danger" style="grid-column:1/-1;">Failed to load listings.</p>`;
    }
}

// Delete product listing
async function deleteListing(productId) {
    if (!confirm("Are you sure you want to delete this listing? This will also remove the product from the marketplace.")) return;
    
    try {
        const res = await fetch(`${API_BASE}/products/${productId}`, {
            method: 'DELETE'
        });
        
        if (res.ok) {
            showToast("Listing deleted successfully.");
            loadProducerDashboard();
        } else {
            showToast("Failed to delete listing", true);
        }
    } catch (e) {
        console.error(e);
        showToast("Network error deleting listing", true);
    }
}

// Render Analytics Charts using Chart.js
function renderProducerCharts(analytics) {
    const revCtx = document.getElementById('revenueChart').getContext('2d');
    const prodCtx = document.getElementById('productChart').getContext('2d');

    // Destroy existing chart instances if they exist to avoid overlapping mouseover elements
    if (revenueChartInstance) revenueChartInstance.destroy();
    if (productChartInstance) productChartInstance.destroy();

    // 1. Sales Trend / Revenue Line Chart
    // Prepare mock trend data based on actual revenue for visuals
    const recentOrders = analytics.recentOrders || [];
    let cumulativeRevenue = 0;
    const trendLabels = [];
    const trendData = [];
    
    // Sort orders chronological for trend line
    [...recentOrders].reverse().forEach(o => {
        cumulativeRevenue += o.totalAmount;
        trendLabels.push(new Date(o.createdAt).toLocaleDateString());
        trendData.push(cumulativeRevenue);
    });

    // Handle empty state
    if (trendLabels.length === 0) {
        trendLabels.push(new Date().toLocaleDateString());
        trendData.push(0);
    }

    revenueChartInstance = new Chart(revCtx, {
        type: 'line',
        data: {
            labels: trendLabels,
            datasets: [{
                label: 'Cumulative Sales (₹)',
                data: trendData,
                borderColor: '#064e3b',
                backgroundColor: 'rgba(6, 78, 59, 0.1)',
                tension: 0.3,
                fill: true,
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { grid: { color: '#f5f5f4' } },
                x: { grid: { display: false } }
            }
        }
    });

    // 2. Top Products Bar Chart
    const topProducts = analytics.topProducts || [];
    const prodLabels = topProducts.map(p => p.productName);
    const prodData = topProducts.map(p => p.quantity);

    productChartInstance = new Chart(prodCtx, {
        type: 'bar',
        data: {
            labels: prodLabels.length > 0 ? prodLabels : ['No Sales yet'],
            datasets: [{
                label: 'Units Sold',
                data: prodData.length > 0 ? prodData : [0],
                backgroundColor: ['#d97706', '#f59e0b', '#fbbf24', '#fcd34d', '#fef3c7'],
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { grid: { color: '#f5f5f4' }, beginAtZero: true },
                x: { grid: { display: false } }
            }
        }
    });
}

// Add Listing modal control
function openAddProductModal() {
    document.getElementById('add-product-modal').classList.remove('d-none');
}

function closeAddProductModal() {
    document.getElementById('add-product-modal').classList.add('d-none');
}

// Submit new product form
async function submitNewProduct(event) {
    event.preventDefault();
    
    const producerInfo = ROLES[currentState.currentRole];
    const name = document.getElementById('p-name').value;
    const category = document.getElementById('p-category').value;
    const price = parseFloat(document.getElementById('p-price').value);
    const unit = document.getElementById('p-unit').value;
    const desc = document.getElementById('p-desc').value;
    const origin = document.getElementById('p-origin').value;
    const stock = parseInt(document.getElementById('p-stock').value);
    const image = document.getElementById('p-image').value;
    
    // Split ingredients by comma
    const ingredientsRaw = document.getElementById('p-ingredients').value;
    const ingredients = ingredientsRaw.split(',').map(s => s.trim()).filter(s => s.length > 0);

    const productData = {
        name: name,
        category: category,
        price: price,
        unit: unit,
        description: desc,
        origin: origin,
        stock: stock,
        imageUrl: image,
        ingredients: ingredients,
        producerId: producerInfo.id,
        producerName: producerInfo.name,
        latitude: producerInfo.lat,
        longitude: producerInfo.lon
    };

    try {
        const res = await fetch(`${API_BASE}/products`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });

        if (res.ok) {
            showToast("Product listing created successfully!");
            closeAddProductModal();
            document.getElementById('add-product-form').reset();
            loadProducerDashboard(); // Refresh metrics and listings
        } else {
            showToast("Failed to create product listing", true);
        }
    } catch (e) {
        console.error(e);
        showToast("Network error creating listing", true);
    }
}

// Helper: Toast Notifications
function showToast(message, isError = false) {
    const toast = document.getElementById('toast-notification');
    const toastMsg = document.getElementById('toast-message');
    
    toastMsg.innerText = message;
    
    if (isError) {
        toast.style.backgroundColor = 'var(--danger)';
        toast.querySelector('.toast-icon').className = 'fa-solid fa-circle-exclamation toast-icon';
    } else {
        toast.style.backgroundColor = 'var(--primary)';
        toast.querySelector('.toast-icon').className = 'fa-solid fa-circle-check toast-icon';
    }

    toast.classList.remove('d-none');
    
    // Auto fade-out after 3 seconds
    setTimeout(() => {
        toast.classList.add('d-none');
    }, 3000);
}
