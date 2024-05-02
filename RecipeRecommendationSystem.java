import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RecipeRecommendationSystem extends JFrame {
    private Connection connection;
    private JTextArea recipeTextArea;
    private JComboBox<String> foodItemsComboBox;
    private JButton recommendButton;
    private JButton searchButton;

    public RecipeRecommendationSystem() {
        super("Recipe Recommendation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add components to the control panel
        foodItemsComboBox = new JComboBox<>();
        controlPanel.add(new JLabel("Select Ingredient:"));
        controlPanel.add(foodItemsComboBox);

        recommendButton = new JButton("Recommend Recipes");
        controlPanel.add(recommendButton);

        searchButton = new JButton("Search by Ingredients");
        controlPanel.add(searchButton);

        // Add control panel to the top of the frame
        add(controlPanel, BorderLayout.NORTH);

        // Create panel for recipe text area
        JPanel recipePanel = new JPanel(new BorderLayout());
        recipePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        recipeTextArea = new JTextArea();
        recipeTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(recipeTextArea);
        recipePanel.add(scrollPane, BorderLayout.CENTER);

        // Add recipe panel to the center of the frame
        add(recipePanel, BorderLayout.CENTER);

        // Initialize buttons and action listeners
        recommendButton.addActionListener(e -> recommendRecipes());
        searchButton.addActionListener(e -> searchRecipes());

        // Connect to the database and load food items
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipeRecDB", "root", "kamal2357");
            loadFoodItems();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database");
        }
    }

    private void loadFoodItems() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT ingredient_name FROM ingredients");
            while (resultSet.next()) {
                foodItemsComboBox.addItem(resultSet.getString("ingredient_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void recommendRecipes() {
        String selectedIngredient = (String) foodItemsComboBox.getSelectedItem();
        if (selectedIngredient == null) {
            JOptionPane.showMessageDialog(this, "Please select an ingredient");
            return;
        }
        recommendRecipes(new String[]{selectedIngredient});
    }

    private void recommendRecipes(String[] ingredients) {
        if (ingredients == null || ingredients.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter at least one ingredient.");
            return;
        }

        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT r.recipe_name, r.instructions, u.username AS creator_username, " +
                    "GROUP_CONCAT(DISTINCT i.ingredient_name ORDER BY i.ingredient_name ASC SEPARATOR ', ') AS ingredients_list, " +
                    "GROUP_CONCAT(DISTINCT c.category_name ORDER BY c.category_name ASC SEPARATOR ', ') AS categories_list, " +
                    "GROUP_CONCAT(DISTINCT t.tag_name ORDER BY t.tag_name ASC SEPARATOR ', ') AS tags_list " +
                    "FROM recipes r " +
                    "INNER JOIN users u ON r.creator_id = u.user_id " +
                    "INNER JOIN recipe_ingredients ri ON r.recipe_id = ri.recipe_id " +
                    "INNER JOIN ingredients i ON ri.ingredient_id = i.ingredient_id " +
                    "INNER JOIN recipe_categories rc ON r.recipe_id = rc.recipe_id " +
                    "INNER JOIN categories c ON rc.category_id = c.category_id " +
                    "INNER JOIN recipe_tags rt ON r.recipe_id = rt.recipe_id " +
                    "INNER JOIN tags t ON rt.tag_id = t.tag_id " +
                    "WHERE i.ingredient_name IN (");

            for (int i = 0; i < ingredients.length; i++) {
                queryBuilder.append("?");
                if (i < ingredients.length - 1) {
                    queryBuilder.append(",");
                }
            }
            queryBuilder.append(") GROUP BY r.recipe_name, r.instructions, u.username");

            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < ingredients.length; i++) {
                preparedStatement.setString(i + 1, ingredients[i].trim());
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                recipeTextArea.setText("No recipes found with the given ingredients.");
            } else {
                ArrayList<String> recipes = new ArrayList<>();
                while (resultSet.next()) {
                    String recipeName = resultSet.getString("recipe_name");
                    recipes.add(recipeName);
                }

                String[] recipeArray = recipes.toArray(new String[0]);
                JComboBox<String> recipeComboBox = new JComboBox<>(recipeArray);
                int option = JOptionPane.showOptionDialog(this, recipeComboBox, "Select Recipe", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                if (option == JOptionPane.OK_OPTION) {
                    String selectedRecipe = (String) recipeComboBox.getSelectedItem();
                    displayRecipeInstructions(selectedRecipe);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to recommend recipes");
        }
    }

    private void searchRecipes() {
        String input = JOptionPane.showInputDialog(this, "Enter ingredients separated by commas:");
        if (input != null && !input.isEmpty()) {
            String[] ingredients = input.split(",");
            recommendRecipes(ingredients);
        }
    }

    private void displayRecipeInstructions(String recipeName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT instructions FROM recipes WHERE recipe_name = ?");
            preparedStatement.setString(1, recipeName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                recipeTextArea.setText(resultSet.getString("instructions"));
            } else {
                recipeTextArea.setText("Instructions not found for the selected recipe.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve instructions");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecipeRecommendationSystem().setVisible(true));
    }
}
