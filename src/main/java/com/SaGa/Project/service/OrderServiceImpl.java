package com.SaGa.Project.service;

import com.SaGa.Project.exception.*;
import com.SaGa.Project.model.Cart;
import com.SaGa.Project.model.Order;
import com.SaGa.Project.model.Product;
import com.SaGa.Project.model.User;
import com.SaGa.Project.repository.CartRepository;
import com.SaGa.Project.repository.OrderRepository;
import com.SaGa.Project.repository.ProductRepository;
import com.SaGa.Project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Order addOrder(Order order) {
        Optional<User> optionalUser = userRepository.findById(order.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            Optional<Cart> cartOptional = cartRepository.findByUserId(user.getUserId());
            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();

                order.setBillingAddress(user.getAddress());
                order.setShippingAddress(user.getAddress());

                order.setItems(cart.getItems());
                order.setTotalPrice(cart.getTotalPrice());
                order.setCreatedAt(new Date());

                cart.getItems().forEach(item -> {
                    Optional<Product> optionalProduct = productRepository.findById(item.getProductId());
                    if (optionalProduct.isPresent()){
                        Product product = optionalProduct.get();

                        if(product.getStock() > 0){
                            product.setStock(product.getStock() - 1);
                            productRepository.save(product);
                        }else {
                            throw new ProductOutOfStockException("Product "+ product.getName() + " is out of stock");
                        }
                    }
                });

                Order savedOrder = orderRepository.save(order);


                List<Order> orders = user.getOrders();
                if (orders != null) {
                    orders.add(savedOrder);
                } else {
                    orders = List.of(savedOrder);
                }
                user.setOrders(orders);


                cartRepository.save(cart);


                userRepository.save(user);

                return savedOrder;
            } else {
                throw new CartNotFoundException("Cart not found for user");
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public Order updateOrder(Order updatedOrder) {
        Optional<Order> optionalOrder = orderRepository.findById(updatedOrder.getId());

        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();

            Optional<User> optionalUser = userRepository.findById(updatedOrder.getUserId());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if (user.getRoles().equals("ADMIN")) {
                    existingOrder.setStatus(updatedOrder.getStatus());

                    return orderRepository.save(existingOrder);
                } else {
                    throw new AdminsCanHandleError("Only admins can update orders.");
                }
            } else {
                throw new UserNotFoundException("User not found");
            }
        } else {
            throw new OrderNotFoundException("Order not found");
        }
    }

    @Override
    public List<Order> getAllOrders(String adminUserId) {
        Optional<User> optionalUser = userRepository.findById(adminUserId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            if (user.getRoles().equals("ADMIN")){
                return orderRepository.findAll();
            }else {
                throw new AdminsCanHandleError("Only admins can view all orders.");
            }
        }else {
            throw new UserNotFoundException("Admin User Not Found");
        }
    }

    @Override
    public Order getOrderById(String orderId, String adminUserId) {
        Optional<User> optionalUser = userRepository.findById(adminUserId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the user has the ADMIN role
            if (user.getRoles().equals("ADMIN")) {
                return orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
            } else {
                throw new AdminsCanHandleError("Only admins can view orders by ID.");
            }
        } else {
            throw new UserNotFoundException("Admin user not found.");
        }
    }
}
