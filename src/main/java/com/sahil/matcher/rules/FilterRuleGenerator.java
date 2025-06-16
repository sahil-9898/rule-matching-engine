package com.sahil.matcher.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//TODO: remove after testing

public class FilterRuleGenerator {

    private static final String[] STRING_FIELD_PATHS = {
            "customer.firstName", "customer.lastName", "customer.email", "customer.address.street",
            "customer.address.city", "customer.address.state", "customer.address.country",
            "order.status", "order.shippingMethod", "order.paymentMethod",
            "product.name", "product.category", "product.description", "product.manufacturer",
            "metadata.source", "metadata.channel", "metadata.tags", "metadata.deviceType"
    };

    private static final String[] NUMBER_FIELD_PATHS = {
            "customer.id", "customer.age", "customer.loyaltyPoints", "customer.address.zipCode",
            "order.id", "order.totalAmount", "order.discount", "order.tax",
            "order.items.quantity", "order.items.price", "order.items.discount",
            "product.id", "product.price", "product.rating", "product.stock",
            "metadata.timestamp", "metadata.version", "metadata.priority"
    };

    private static final String[] BOOLEAN_FIELD_PATHS = {
            "customer.isVerified", "customer.isSubscribed", "customer.isPremium",
            "order.isGift", "order.isPaid", "order.isShipped", "order.isReturned",
            "product.isAvailable", "product.isOnSale", "product.isFeatured",
            "metadata.isValid", "metadata.isProcessed", "metadata.isDeleted"
    };

    private static final String[] STRING_VALUES = {
            "John", "Mary", "Smith", "Jones", "example@email.com", "Main Street", "New York",
            "California", "USA", "Canada", "Pending", "Shipped", "Delivered", "Credit Card",
            "PayPal", "iPhone", "Laptop", "Electronics", "Clothing", "Apple", "Samsung",
            "High Quality", "Web", "Mobile", "Social", "premium", "standard", "Desktop", "Mobile"
    };

//    public static void main(String[] args) throws Exception {
//        // Generate 10,000 random conditional filter rules
//        List<FilterRule> rules = generateRandomFilterRules(10000);
//
//        // Generate a sample JSON message
//        String jsonMessage = generateSampleJsonMessage();
//
//        System.out.println("Generated " + rules.size() + " random filter rules");
//        System.out.println("\nSample JSON Message:");
//        System.out.println(jsonMessage);
//
//        final RuleSet ruleSet = new RuleSet(rules);
//        final JsonMessageProcessor messageProcessor = new JsonMessageProcessor();
//        for (int i = 0; i < 10_000; i++) {
//            final Object[] fieldValues = messageProcessor.getProcessedFields(messageProcessor.deserialize(jsonMessage), ruleSet.fieldPaths);
//            ruleSet.matches(fieldValues);
//        }
//    }

    public static List<FilterRule> generateRandomFilterRules(int count) {
        List<FilterRule> rules = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            // Generate a conditional filter rule (with 2-5 child rules)
            int childRuleCount = ThreadLocalRandom.current().nextInt(2, 6);
            List<FilterRule> childRules = new ArrayList<>(childRuleCount);

            for (int j = 0; j < childRuleCount; j++) {
                childRules.add(generateRandomSimpleFilterRule());
            }

            Condition condition = ThreadLocalRandom.current().nextBoolean() ? Condition.AND : Condition.OR;
            boolean isNotRule = ThreadLocalRandom.current().nextDouble() < 0.1; // 10% chance of NOT rule

            rules.add(new ConditionalFilterRule(condition, isNotRule, childRules));
        }

        return rules;
    }

    private static SimpleFilterRule generateRandomSimpleFilterRule() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Determine data type
        DataType dataType;
        String fieldPath;
        Object value = null;
        Object toValue = null;
        Operator operator;
        boolean isNotRule = random.nextDouble() < 0.1; // 10% chance of NOT rule
        boolean isCaseSensitive = random.nextBoolean();

        int typeChoice = random.nextInt(3);
        switch (typeChoice) {
            case 0 -> {
                dataType = DataType.STRING;
                fieldPath = STRING_FIELD_PATHS[random.nextInt(STRING_FIELD_PATHS.length)];

                // Choose an appropriate operator for strings
                Operator[] stringOperators = {
                        Operator.CONTAINS, Operator.NOT_CONTAINS, Operator.STARTSWITH, Operator.ENDSWITH,
                        Operator.EQUALS, Operator.NOT_EQUALS, Operator.ISBLANK, Operator.ISNOTBLANK
                };
                operator = stringOperators[random.nextInt(stringOperators.length)];

                if (operator != Operator.ISBLANK && operator != Operator.ISNOTBLANK) {
                    value = STRING_VALUES[random.nextInt(STRING_VALUES.length)];
                }
            }
            case 1 -> {
                dataType = DataType.NUMBER;
                fieldPath = NUMBER_FIELD_PATHS[random.nextInt(NUMBER_FIELD_PATHS.length)];

                // Choose an appropriate operator for numbers
                Operator[] numberOperators = {
                        Operator.EQUALS, Operator.NOT_EQUALS, Operator.LESSTHAN, Operator.LESSTHANEQUALS,
                        Operator.GREATERTHAN, Operator.GREATERTHANEQUALS, Operator.BETWEEN
                };
                operator = numberOperators[random.nextInt(numberOperators.length)];

                value = random.nextInt(1, 10000);
                if (operator == Operator.BETWEEN) {
                    int firstValue = random.nextInt(1, 5000);
                    int secondValue = random.nextInt(5001, 10000);
                    value = firstValue;
                    toValue = secondValue;
                }
            }
            default -> {
                dataType = DataType.BOOLEAN;
                fieldPath = BOOLEAN_FIELD_PATHS[random.nextInt(BOOLEAN_FIELD_PATHS.length)];

                // Only equals and not_equals make sense for booleans
                operator = random.nextBoolean() ? Operator.EQUALS : Operator.NOT_EQUALS;
                value = random.nextBoolean();
            }
        }

        return new SimpleFilterRule(fieldPath, dataType, value, toValue, operator, isNotRule, isCaseSensitive);
    }

    public static String generateSampleJsonMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // Customer information
        ObjectNode customerNode = rootNode.putObject("customer");
        customerNode.put("id", ThreadLocalRandom.current().nextInt(10000, 99999));
        customerNode.put("firstName", getRandomElement(STRING_VALUES));
        customerNode.put("lastName", getRandomElement(STRING_VALUES));
        customerNode.put("email", getRandomElement(STRING_VALUES) + "@example.com");
        customerNode.put("age", ThreadLocalRandom.current().nextInt(18, 80));
        customerNode.put("loyaltyPoints", ThreadLocalRandom.current().nextInt(0, 5000));
        customerNode.put("isVerified", ThreadLocalRandom.current().nextBoolean());
        customerNode.put("isSubscribed", ThreadLocalRandom.current().nextBoolean());
        customerNode.put("isPremium", ThreadLocalRandom.current().nextBoolean());

        // Customer address
        ObjectNode addressNode = customerNode.putObject("address");
        addressNode.put("street", getRandomElement(STRING_VALUES) + " Street");
        addressNode.put("city", getRandomElement(STRING_VALUES));
        addressNode.put("state", getRandomElement(STRING_VALUES));
        addressNode.put("country", getRandomElement(STRING_VALUES));
        addressNode.put("zipCode", ThreadLocalRandom.current().nextInt(10000, 99999));

        // Order information
        ObjectNode orderNode = rootNode.putObject("order");
        orderNode.put("id", ThreadLocalRandom.current().nextInt(100000, 999999));
        orderNode.put("status", getRandomElement(new String[]{"Pending", "Shipped", "Delivered", "Cancelled"}));
        orderNode.put("totalAmount", ThreadLocalRandom.current().nextDouble(10.0, 1000.0));
        orderNode.put("discount", ThreadLocalRandom.current().nextDouble(0.0, 100.0));
        orderNode.put("tax", ThreadLocalRandom.current().nextDouble(0.0, 50.0));
        orderNode.put("shippingMethod", getRandomElement(new String[]{"Standard", "Express", "Overnight"}));
        orderNode.put("paymentMethod", getRandomElement(new String[]{"Credit Card", "PayPal", "Apple Pay", "Google Pay"}));
        orderNode.put("isGift", ThreadLocalRandom.current().nextBoolean());
        orderNode.put("isPaid", ThreadLocalRandom.current().nextBoolean());
        orderNode.put("isShipped", ThreadLocalRandom.current().nextBoolean());
        orderNode.put("isReturned", ThreadLocalRandom.current().nextBoolean());

        // Order items
        ArrayNode itemsArray = orderNode.putArray("items");
        int itemCount = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i = 0; i < itemCount; i++) {
            ObjectNode itemNode = itemsArray.addObject();
            itemNode.put("productId", ThreadLocalRandom.current().nextInt(1000, 9999));
            itemNode.put("quantity", ThreadLocalRandom.current().nextInt(1, 10));
            itemNode.put("price", ThreadLocalRandom.current().nextDouble(5.0, 500.0));
            itemNode.put("discount", ThreadLocalRandom.current().nextDouble(0.0, 50.0));
        }

        // Product information
        ObjectNode productNode = rootNode.putObject("product");
        productNode.put("id", ThreadLocalRandom.current().nextInt(1000, 9999));
        productNode.put("name", getRandomElement(STRING_VALUES) + " " + getRandomElement(STRING_VALUES));
        productNode.put("category", getRandomElement(new String[]{"Electronics", "Clothing", "Books", "Home", "Beauty"}));
        productNode.put("description", "High quality " + getRandomElement(STRING_VALUES) + " product");
        productNode.put("manufacturer", getRandomElement(new String[]{"Apple", "Samsung", "Sony", "Nike", "Adidas"}));
        productNode.put("price", ThreadLocalRandom.current().nextDouble(10.0, 1000.0));
        productNode.put("rating", ThreadLocalRandom.current().nextDouble(1.0, 5.0));
        productNode.put("stock", ThreadLocalRandom.current().nextInt(0, 1000));
        productNode.put("isAvailable", ThreadLocalRandom.current().nextBoolean());
        productNode.put("isOnSale", ThreadLocalRandom.current().nextBoolean());
        productNode.put("isFeatured", ThreadLocalRandom.current().nextBoolean());

        // Metadata
        ObjectNode metadataNode = rootNode.putObject("metadata");
        metadataNode.put("timestamp", System.currentTimeMillis());
        metadataNode.put("source", getRandomElement(new String[]{"Web", "Mobile", "API", "Store"}));
        metadataNode.put("channel", getRandomElement(new String[]{"Direct", "Affiliate", "Social", "Email"}));
        metadataNode.put("version", "1." + ThreadLocalRandom.current().nextInt(0, 10));
        metadataNode.put("priority", ThreadLocalRandom.current().nextInt(1, 5));

        ArrayNode tagsArray = metadataNode.putArray("tags");
        int tagCount = ThreadLocalRandom.current().nextInt(1, 5);
        for (int i = 0; i < tagCount; i++) {
            tagsArray.add(getRandomElement(STRING_VALUES));
        }

        metadataNode.put("deviceType", getRandomElement(new String[]{"Desktop", "Mobile", "Tablet"}));
        metadataNode.put("isValid", ThreadLocalRandom.current().nextBoolean());
        metadataNode.put("isProcessed", ThreadLocalRandom.current().nextBoolean());
        metadataNode.put("isDeleted", ThreadLocalRandom.current().nextBoolean());

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}