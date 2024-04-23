CREATE DATABASE IF NOT EXISTS recipeRecDB;
USE recipeRecDB;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL
);

CREATE TABLE ingredients (
    ingredient_id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_name VARCHAR(100) NOT NULL
);

CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE tags (
    tag_id INT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(100) NOT NULL
);

CREATE TABLE recipes (
    recipe_id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_name VARCHAR(200) NOT NULL,
    instructions TEXT NOT NULL,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES users(user_id)
);

CREATE TABLE recipe_ingredients (
    recipe_id INT,
    ingredient_id INT,
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

CREATE TABLE recipe_categories (
    recipe_id INT,
    category_id INT,
    PRIMARY KEY (recipe_id, category_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE recipe_tags (
    recipe_id INT,
    tag_id INT,
    PRIMARY KEY (recipe_id, tag_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

INSERT INTO users (username) VALUES
('user1'),
('user2'),
('user3');


INSERT INTO ingredients (ingredient_name) VALUES
('Flour'),
('Sugar'),
('Eggs'),
('Milk'),
('Butter'),
('Salt'),
('Vanilla extract'),
('Baking powder'),
('Chocolate chips'),
('Bananas');


INSERT INTO categories (category_name) VALUES
('Dessert'),
('Breakfast'),
('Main Course'),
('Appetizer'),
('Side Dish');


INSERT INTO tags (tag_name) VALUES
('Easy'),
('Healthy'),
('Quick'),
('Vegetarian');


INSERT INTO recipes (recipe_name, instructions, creator_id) VALUES
('Chocolate Chip Cookies', '1. Preheat oven to 350°F (175°C). 2. In a medium bowl, cream together the butter, white sugar, and brown sugar. 3. Beat in the eggs one at a time, then stir in the vanilla. 4. Dissolve baking soda in hot water. Add to batter along with salt. 5. Stir in flour, chocolate chips, and nuts. 6. Drop by large spoonfuls onto ungreased pans. 7. Bake for about 10 minutes in the preheated oven, or until edges are nicely browned.', 1),
('Banana Bread', '1. Preheat oven to 350°F (175°C). 2. In a large bowl, mash the ripe bananas with a fork until smooth. 3. Stir in the melted butter. 4. Mix in the baking soda and salt. 5. Stir in the sugar, beaten egg, and vanilla extract. 6. Mix in the flour until just incorporated. 7. Pour the batter into a greased 9x5 inch loaf pan. 8. Bake in preheated oven for 60 to 65 minutes, or until a toothpick inserted into the center comes out clean. 9. Let bread cool in pan for 10 minutes, then turn out onto a wire rack.', 2);


INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES
(1, 1), -- Flour
(1, 2), -- Sugar
(1, 3), -- Eggs
(1, 4), -- Milk
(1, 5), -- Butter
(1, 6), -- Salt
(1, 7), -- Vanilla extract
(1, 8), -- Baking powder
(1, 9); -- Chocolate chips

-- Recipe 2: Banana Bread
INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES
(2, 1), -- Flour
(2, 5), -- Butter
(2, 3), -- Eggs
(2, 10); -- Bananas


INSERT INTO recipe_categories (recipe_id, category_id) VALUES
(1, 1); -- Dessert

-- Recipe 2: Banana Bread
INSERT INTO recipe_categories (recipe_id, category_id) VALUES
(2, 2); -- Breakfast



-- Recipe 1: Chocolate Chip Cookies
INSERT INTO recipe_tags (recipe_id, tag_id) VALUES
(1, 1), -- Easy
(1, 3); -- Quick

-- Recipe 2: Banana Bread
INSERT INTO recipe_tags (recipe_id, tag_id) VALUES
(2, 1), -- Easy
(2, 4); -- Vegetarian

