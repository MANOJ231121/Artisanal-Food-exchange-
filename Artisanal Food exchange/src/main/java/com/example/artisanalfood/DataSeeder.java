package com.example.artisanalfood;

import com.example.artisanalfood.model.*;
import com.example.artisanalfood.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final OrderRepository orderRepository;

    public DataSeeder(UserRepository userRepository, 
                      ProductRepository productRepository, 
                      ReviewRepository reviewRepository, 
                      SubscriptionRepository subscriptionRepository, 
                      OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clean database collections for fresh local simulation run
        userRepository.deleteAll();
        productRepository.deleteAll();
        reviewRepository.deleteAll();
        subscriptionRepository.deleteAll();
        orderRepository.deleteAll();

        System.out.println("Cleaned database collections. Seeding fresh local simulation data...");

        // 1. Seed Users (Producers & Consumers)
        User p1 = new User("prod_1", "Himachal Mountain Farms", "contact@mountainfarms.com", 
                User.Role.PRODUCER, 
                "We harvest wild forest honey, organic walnuts, and crisp apples directly from high-altitude orchards in Shimla. Sustainable farming practices since 1994.", 
                31.1048, 77.1734, "Kufri Bypass, Shimla, Himachal Pradesh", 4.9);
        
        User p2 = new User("prod_2", "Ganga Valley Dairy", "cheese@gangavalley.com", 
                User.Role.PRODUCER, 
                "Traditional, slow-aged artisanal goat cheese, wood-fired cottage cheese, and pure A2 cow ghee. Sourced from local Himalayan herds.", 
                30.0869, 78.2676, "Tapovan, Rishikesh, Uttarakhand", 4.7);

        User c1 = new User("user_1", "Manoj", "manoj@example.com", 
                User.Role.CONSUMER, 
                null, 28.6139, 77.2090, "Connaught Place, New Delhi", 0.0); // Delhi coordinates

        User c2 = new User("user_2", "Ritika", "ritika@example.com", 
                User.Role.CONSUMER, 
                null, 30.3165, 78.0322, "Rajpur Road, Dehradun", 0.0); // Dehradun coordinates

        userRepository.saveAll(Arrays.asList(p1, p2, c1, c2));

        // 2. Seed Products
        Product prod1 = new Product("honey_1", "Wildflower Forest Honey", 
                "100% raw, unfiltered honey harvested from wild forest beehives in the valleys of Shimla. Rich in antioxidants and natural nutrients.", 
                450.00, "500g Glass Jar", 
                "images/honey.png", 
                "Shimla Forest Reserve", 
                Arrays.asList("Raw Flower Nectar", "Wild Bee Pollen"), 
                "prod_1", "Himachal Mountain Farms", "Honey & Sweeteners", 20, 31.1048, 77.1734);

        Product prod2 = new Product("apples_1", "Royal Delicious Apples", 
                "Crisp, sweet, and hand-picked at peak ripeness. Grown in high-altitude soil with zero chemical pesticides.", 
                180.00, "1 kg Box", 
                "images/apples.png", 
                "Kufri Orchards, Shimla", 
                Arrays.asList("Fresh Shimla Apples"), 
                "prod_1", "Himachal Mountain Farms", "Fresh Produce", 80, 31.1048, 77.1734);

        Product prod3 = new Product("cheese_1", "Artisanal Herb Goat Cheese", 
                "Soft, creamy goat cheese logs rolled in fresh rosemary, thyme, and cracked black pepper. Matured for 3 weeks.", 
                580.00, "200g Log", 
                "images/cheese.png", 
                "Rishikesh Foothills", 
                Arrays.asList("Pasteurized Goat Milk", "Fresh Rosemary", "Fresh Thyme", "Black Pepper", "Sea Salt"), 
                "prod_2", "Ganga Valley Dairy", "Dairy & Cheese", 15, 30.0869, 78.2676);

        Product prod4 = new Product("ghee_1", "A2 Organic Cow Ghee", 
                "Slow-cooked clarified butter made from the milk of free-roaming indigenous cows. Prepared using the traditional Bilona method.", 
                850.00, "1 Liter Jar", 
                "images/ghee.png", 
                "Ganga Valley Grasslands", 
                Arrays.asList("Pure Clarified Butter Fat"), 
                "prod_2", "Ganga Valley Dairy", "Dairy & Cheese", 30, 30.0869, 78.2676);

        productRepository.saveAll(Arrays.asList(prod1, prod2, prod3, prod4));

        // 3. Seed Reviews
        Review r1 = new Review("rev_1", "honey_1", "user_1", "Manoj", 5, 
                "Incredibly rich floral aroma. You can taste the authenticity! Will definitely order monthly.", 
                LocalDateTime.now().minusDays(10));
        Review r2 = new Review("rev_2", "cheese_1", "user_2", "Ritika", 4, 
                "Extremely creamy and the herbs are very fresh. Goes perfectly on sourdough toast.", 
                LocalDateTime.now().minusDays(5));
        Review r3 = new Review("rev_3", "apples_1", "user_1", "Manoj", 5, 
                "So juicy and crisp! Much better than the cold storage ones from local supermarkets.", 
                LocalDateTime.now().minusDays(2));

        reviewRepository.saveAll(Arrays.asList(r1, r2, r3));

        // 4. Seed Subscriptions
        Subscription sub1 = new Subscription("sub_1", "user_1", "honey_1", "Wildflower Forest Honey", 
                "Himachal Mountain Farms", Subscription.Frequency.MONTHLY, Subscription.Status.ACTIVE, 1, 
                LocalDate.now().plusMonths(1));
        
        Subscription sub2 = new Subscription("sub_2", "user_2", "ghee_1", "A2 Organic Cow Ghee", 
                "Ganga Valley Dairy", Subscription.Frequency.MONTHLY, Subscription.Status.ACTIVE, 2, 
                LocalDate.now().plusMonths(1));

        subscriptionRepository.saveAll(Arrays.asList(sub1, sub2));

        // 5. Seed Orders (sales history)
        Order o1 = new Order("ord_1", "user_1", "Manoj", "honey_1", "Wildflower Forest Honey", 
                "prod_1", 450.00, 2, 900.00, LocalDateTime.now().minusDays(25));
        Order o2 = new Order("ord_2", "user_2", "Ritika", "cheese_1", "Artisanal Herb Goat Cheese", 
                "prod_2", 580.00, 1, 580.00, LocalDateTime.now().minusDays(14));
        Order o3 = new Order("ord_3", "user_1", "Manoj", "apples_1", "Royal Delicious Apples", 
                "prod_1", 180.00, 4, 720.00, LocalDateTime.now().minusDays(3));
        Order o4 = new Order("ord_4", "user_2", "Ritika", "ghee_1", "A2 Organic Cow Ghee", 
                "prod_2", 850.00, 1, 850.00, LocalDateTime.now().minusDays(1));

        orderRepository.saveAll(Arrays.asList(o1, o2, o3, o4));

        System.out.println("Data seeding completed successfully!");
    }
}
