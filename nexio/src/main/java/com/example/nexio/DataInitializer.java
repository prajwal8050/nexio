package com.example.nexio;

import com.example.nexio.model.ServiceItem;
import com.example.nexio.model.User;
import com.example.nexio.model.WaitContent;
import com.example.nexio.repository.ServiceRepository;
import com.example.nexio.repository.UserRepository;
import com.example.nexio.repository.WaitContentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

        @Bean
        CommandLineRunner initDatabase(ServiceRepository repository, UserRepository userRepository,
                        WaitContentRepository waitContentRepository) {
                return args -> {
                        // Cleanup: Ensure no users have null roles (prevents frontend crashes)
                        userRepository.findAll().stream()
                                        .filter(u -> u.getRole() == null)
                                        .forEach(u -> {
                                                u.setRole("USER");
                                                userRepository.save(u);
                                        });

                        // Seed Agent
                        userRepository.findByEmail("agent@nexio.com").ifPresentOrElse(
                                        u -> {
                                                u.setRole("AGENT");
                                                u.setPassword("agent123");
                                                userRepository.save(u);
                                        },
                                        () -> userRepository.save(new User(null, "Agent Nexio", "agent@nexio.com",
                                                        "+1 888 000 000", "agent123", "AGENT")));

                        // Seed Admin
                        userRepository.findByEmail("admin@nexio.com").ifPresentOrElse(
                                        u -> {
                                                u.setRole("ADMIN");
                                                u.setPassword("admin123");
                                                userRepository.save(u);
                                        },
                                        () -> userRepository.save(new User(null, "Super Admin", "admin@nexio.com",
                                                        "+1 777 000 000", "admin123", "ADMIN")));

                        // Seed Demo User
                        userRepository.findByEmail("demo@nexio.com").ifPresentOrElse(
                                        u -> {
                                                u.setRole("USER");
                                                u.setPassword("demo123");
                                                userRepository.save(u);
                                        },
                                        () -> userRepository.save(new User(null, "Demo User", "demo@nexio.com",
                                                        "+1 123 456 789", "demo123", "USER")));

                        List<ServiceItem> services = Arrays.asList(
                                        new ServiceItem(null, "City General Hospital",
                                                        "General OPD and Emergency services with expert doctors.",
                                                        "Healthcare",
                                                        "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&q=80&w=800",
                                                        15),
                                        new ServiceItem(null, "Elite Hair Studio",
                                                        "Premium hair styling and grooming for men and women.",
                                                        "Beauty",
                                                        "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&q=80&w=800",
                                                        30),
                                        new ServiceItem(null, "Global Trust Bank",
                                                        "Personal and business banking services with minimal wait.",
                                                        "Finance",
                                                        "https://images.unsplash.com/photo-1501167786227-4cba60f6d58f?auto=format&fit=crop&q=80&w=800",
                                                        10),
                                        new ServiceItem(null, "Passport Office",
                                                        "Government document verification and passport processing.",
                                                        "Government",
                                                        "https://images.unsplash.com/photo-1526948531399-320e7e40f0ca?auto=format&fit=crop&q=80&w=800",
                                                        60),
                                        new ServiceItem(null, "Apple Store - Mall",
                                                        "Sales and technical support for all your Apple products.",
                                                        "Retail",
                                                        "https://images.unsplash.com/photo-1491933382434-500287f9b54b?auto=format&fit=crop&q=80&w=800",
                                                        20),
                                        new ServiceItem(null, "Tech Service Center",
                                                        "Certified repairs and support for electronics and appliances.",
                                                        "Service Center",
                                                        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&q=80&w=800",
                                                        25));

                        for (ServiceItem s : services) {
                                // Check if exists by name
                                if (repository.findAll().stream()
                                                .noneMatch(existing -> existing.getName().equals(s.getName()))) {
                                        repository.save(s);
                                }
                        }

                        // Seed Wait Content
                        if (waitContentRepository.count() == 0) {
                                waitContentRepository.saveAll(Arrays.asList(
                                                new WaitContent("Health Digest",
                                                                "A quick 3-min read on hydration and wellness.",
                                                                "ARTICLE", "book-open", "#", 0),
                                                new WaitContent("Focus Plus",
                                                                "Lofi playlist for a calm and productive session.",
                                                                "MUSIC", "music", "#", 0),
                                                new WaitContent("Nearby Perks",
                                                                "10% off at 'The Bean Corner' just around the corner.",
                                                                "PERK", "coffee", "#", 0)));
                        }
                };
        }
}
