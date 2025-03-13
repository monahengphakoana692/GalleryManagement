package com.example.gallerymanagement;

import com.jpro.webapi.WebAPI;
import com.jpro.webapi.JProApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends JProApplication
{

    //setting constant thumbnail size within the program
    private static final int THUMBNAIL_WIDTH = 80;
    private static final int THUMBNAIL_HEIGHT = 80;
    private static final int IMAGES_PER_ROW = 13;

    public VBox root = new VBox();
    public HBox rootForControlsAndRows = new HBox(10);
    public VBox rowsContainer = new VBox(10);
    public int colorChangeTracker = 0; //initializing
    public int BrowserTracker = 0;

    // Add a member flag to check if the customization pane already exists
    private boolean isCustomizationAdded = false;

    // Buttons (Right Side)
    Button allImages = new Button("ALL IMAGES");
    Button newImage = new Button("SHARE");
    Label resizing = new Label("Resize Images");
    Label desize = new Label("Desize Images");
    Label colorChange = new Label("change Color");


    @Override
    public void start(Stage stage)
    {
        try {

            root.setId("Background");
            topMenu(root);
            allImages.setOnAction(event->
            {
                try
                {
                    addThumbnails(root,loadImages());
                    newImage.setStyle("-fx-background-color : linear-gradient(to right, #2ecc71, #27ae60);");
                    allImages.setStyle("-fx-background-color : linear-gradient(to right, #f2709c, #ff9472);");
                } catch (Exception e)
                {
                    showAlert("Image error: " + e.toString());
                }

            });
            newImage.setOnAction(event->
            {
                try
                {
                    addThumbnails(root,loadImages());
                    allImages.setStyle("-fx-background-color :linear-gradient(to right, #2ecc71, #27ae60);");
                    newImage.setStyle("-fx-background-color : linear-gradient(to right, #f2709c, #ff9472);");

                } catch (Exception e)
                {
                    showAlert("Image error: " + e.toString());
                }

            });

            allImages.setPrefSize(140, 40);
            newImage.setPrefSize(140, 40);
            // Load images and add them in rows
            addThumbnails(root, loadImages());

            // Wrap in ScrollPane
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene scene = new Scene(scrollPane, 1555, 800);
            displayScene(stage, scene);

        } catch (Exception e) {
            showAlert("Image issues: " + e);
        }
    }

    public void displayScene(Stage stage, Scene scene) {
        try
        {
            scene.getStylesheets().add(getClass().getResource("/Style1.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Gallery Management");
            if (WebAPI.isBrowser())
            {
                WebAPI webAPI = WebAPI.getWebAPI(stage);
                webAPI.executeScript("alert('Hello from JavaFX in the browser!')");
            }
            stage.show();
        } catch (Exception e) {
            showAlert("Something went wrong: " + e);
        }
    }

    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }


    public void addThumbnails(VBox root, List<ImageView> imageList) throws Exception {
        try {
            HBox currentRow = new HBox(10); // Horizontal row for images
            // Main layout container
            List<HBox> rows = new ArrayList<>();
            VBox customization = new VBox(10); // Pane for customization control
            VBox imageContainer;

            for (int i = 0; i < imageList.size(); i++)
            {
                if (i % IMAGES_PER_ROW == 0 && i != 0)
                { // changing the row to the next
                    rows.add(currentRow);
                    currentRow = new HBox(10);
                }

                // Container for image
                imageContainer = new VBox(5);
                //imageContainer.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: lightgray;");
                imageContainer.setMaxWidth(imageList.get(i).getFitWidth()); // Adjust width to the image s size
                imageContainer.setMaxHeight(imageList.get(i).getFitHeight());
                imageContainer.setId("eachImage");

                // Add image and label inside the container
                imageContainer.getChildren().addAll(imageList.get(i));

                // Set click event to enlarge image
                int finalI = i;
                imageList.get(i).setOnMouseClicked(event -> {
                    showFullImage(root, imageList.get(finalI), loadImages());// passing pressed image
                });

                // Add container to row
                currentRow.getChildren().add(imageContainer);
            }

            rows.add(currentRow); // Adding last row

            // clear and Add all rows to the VBox
            rowsContainer.getChildren().clear();
            rowsContainer.getChildren().addAll(rows);


            if (!isCustomizationAdded)
            {
                //customization.setPrefSize(590, 300);
                customization.setMaxWidth(590);
                customization.setMinHeight(300);
                //customization.setStyle("-fx-background-color: lime;");
                customization.setId("Customization");
                Label Title = new Label("Customization Panel");
                Title.setStyle("-fx-font-size:20; -fx-font-weight:bold;");

                customization.getChildren().add(Title);
                customization.getChildren().addAll(CustomLabels(imageList, customization, rowsContainer, rows, currentRow)); // adding labels
                customization.setMaxWidth(200);


                // Add customization on the left and rows on the right

                rootForControlsAndRows.setId("controlAndRows");
                rootForControlsAndRows.setPadding(new Insets(10, 10, 10, 10));

                // Remove existing customization and rowsContainer if present
                rootForControlsAndRows.getChildren().removeAll(customization, rowsContainer);
                rootForControlsAndRows.getChildren().addAll(customization, rowsContainer); // adding children

                root.getChildren().remove(rootForControlsAndRows);
                root.getChildren().add(rootForControlsAndRows);

                // Set flag to indicate customization has been added
                isCustomizationAdded = true;
            }

        } catch (Exception e) {
            showAlert("Thumbnails:" + e.toString());
        }
    }



    public void showFullImage(VBox root, ImageView imageView, List<ImageView> listOfImages)//for viewing single image fully
    {
        try {
            HBox fullImagePane = new HBox(20);


            fullImagePane.setAlignment(Pos.CENTER);
            fullImagePane.setStyle("-fx-background-color:black;");

            GaussianBlur blur = new GaussianBlur();
            blur.setRadius(10);

            root.setEffect(blur);
            root.setDisable(true);

            ImageView fullImageView = new ImageView(imageView.getImage());
            fullImageView.setPreserveRatio(true);
            fullImageView.setFitWidth(400);  // Initial size
            fullImageView.setFitHeight(400);

            for (int i = 0; i < listOfImages.size(); i++)
            {
                if (imageView.getImage() != null && listOfImages.get(i).getImage() != null)
                {
                    if (listOfImages.get(i).getImage().getUrl().equals(imageView.getImage().getUrl()))
                    {
                        BrowserTracker = i;
                        break;
                    }
                }
            }

            // Navigation Labels
            Label forward = new Label(">");
            Label backward = new Label("<");

            forward.setId("nevLabs");
            backward.setId("nevLabs");



            forward.setOnMouseClicked(event ->
            {
                if (BrowserTracker < listOfImages.size() - 1)
                {
                    BrowserTracker++;
                    fullImageView.setImage(listOfImages.get(BrowserTracker).getImage());
                }
            });

            backward.setOnMouseClicked(event ->
            {
                //browsing through images
                if (BrowserTracker > 0) {
                    BrowserTracker--;//tracking images
                    fullImageView.setImage(listOfImages.get(BrowserTracker).getImage());
                }
            });

            // Ensure imageHolder remains fixed
            StackPane imageHolder = new StackPane();
            imageHolder.setPrefSize(600, 600); // Fixed size
            imageHolder.setStyle("-fx-background-color:black;");

            // Ensure images resize properly without breaking layout
            fullImageView.fitWidthProperty().bind(imageHolder.widthProperty());
            fullImageView.fitHeightProperty().bind(imageHolder.heightProperty());

            imageHolder.getChildren().add(fullImageView);

            // Adding space to keep labels in position
            Region space = new Region();
            space.setPrefSize(50, 0); // Space for labels to avoid disturbance

            fullImagePane.getChildren().addAll(space,backward, imageHolder,forward);

            Stage fullImageStage = new Stage();
            fullImageStage.setOnCloseRequest(event ->
            {
                //disabling the gallery when there is a full image view
                root.setDisable(false);
                root.setEffect(null);
            });

            //opening new Scene for new Image view , in full size
            Scene fullImageScene = new Scene(fullImagePane, 1500, 750);
            fullImageScene.getStylesheets().add(getClass().getResource("/Style1.css").toExternalForm());
            fullImageStage.setScene(fullImageScene);
            fullImageStage.initStyle(StageStyle.UTILITY);//disabling mini buttons
            fullImageStage.setTitle("Full Image View");
            fullImageStage.show();

        } catch (Exception e)
        {
            showAlert("**" + e.toString());
        }
    }

    public List<ImageView> loadImages()//loading images from the folder
    {
        List<ImageView> imageViews = new ArrayList<>();

        File Fileimage = new File("src//main//resources//gallaxy");

        if (Fileimage.exists() && Fileimage.isDirectory())//verifying
        {
            File[] files = Fileimage.listFiles();//releasing the file from the Images file

            if (files != null)
            {
                for (File current : files)//extracting files
                {
                    if (current.isFile() && (current.getName().endsWith(".jpg") || current.getName().endsWith(".jpeg")))//filtering fileExtension
                    {

                        Image image = new Image(current.toURI().toString());
                        ImageView imageView = new ImageView(image);
                        //setting the width for each imageView
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(THUMBNAIL_WIDTH);//
                        imageView.setFitHeight(THUMBNAIL_HEIGHT);

                        imageViews.add(imageView);
                    }
                }
            }
        }

        return imageViews;//returning list of ImageViews with each holding the image
    }

    public void topMenu(VBox root)//for the top Menu
    {
        HBox topPane = new HBox(10);
        String[] color1 = {
                "-fx-background-color: linear-gradient(to right, #8E2DE2, #4A00E0);",  // Deep purple to dark blue
                "-fx-background-color: linear-gradient(to right, #FF512F, #DD2476);",  // Reddish-orange to magenta
                "-fx-background-color: linear-gradient(to right, #11998E, #38EF7D);",  // Deep teal to fresh green
                "-fx-background-color: linear-gradient(to right, #FC466B, #3F5EFB);"   // Pinkish-red to vibrant blue
        };

        // Labels (Left Side)
        Label gallery = new Label("Gallery");
        Label colorChange = new Label("change colors");

        colorChange.setId("ColorChangeTopMenu");
        gallery.setId("Gallery");
        Label photos = new Label("Photos");
        photos.setId("photos");

        colorChange.setOnMouseClicked(event -> {
            topPane.setStyle(color1[colorChangeTracker]);
        });
        colorChange.setId("EachLabel");



        // Spacer to push buttons to the right
        Region spacer = new Region();
        spacer.setPrefSize(400,0);//spacing to move buttons towards the right

        // Add elements to HBox
        topPane.getChildren().addAll(colorChange,gallery, photos, spacer, allImages, newImage);

        topPane.setId("topPane");

        root.getChildren().add(topPane);
    }

    public List<Label> CustomLabels(List<ImageView> imageViews,VBox customs,VBox rowContainer,List<HBox> rows,HBox currentRow)
    {
        List<Label> labels = new ArrayList<>();

        resizing.setOnMouseClicked(event ->
        {
            resizeImage(imageViews);
        });
        desize.setOnMouseClicked(event ->
        {
            desizeImage(imageViews);
        });
        colorChange.setOnMouseClicked(event->
        {
            rootForControlsAndRowsColorChanger(customs,rowContainer,rows,currentRow);//areas to change the color on click
        });


        labels.add(colorChange);
        labels.add(resizing);
        labels.add(desize);

        for(int i=0;i<labels.size();i++)
        {
            labels.get(i).setId("EachLabel");//adding css ID to each label
        }

       return labels;//returning an array list of labels for color change and resizing
    }

    public void resizeImage(List<ImageView> resizeImage)//increasing the pane holding images
    {
         for(int i =0;i<resizeImage.size();i++)
        {
            double height = resizeImage.get(i).getFitHeight();
            double width = resizeImage.get(i).getFitHeight();
            //adding ten to the existing
            width = width + 10;
            height = height + 10;
            resizeImage.get(i).setFitHeight(height);
            resizeImage.get(i).setFitWidth(width);

        }
        try
        {

            //addThumbnails(resizeImage);//resized images
        } catch (Exception e)
        {
            showAlert("-" + e.toString());
        }
    }

    public void desizeImage(List<ImageView> resizeImage)//decreasing the pane holding images
    {
        for(int i =0;i<resizeImage.size();i++)
        {
            double height = resizeImage.get(i).getFitHeight();
            double width = resizeImage.get(i).getFitHeight();
            width = width - 10;
            height = height - 10;
            resizeImage.get(i).setFitHeight(height);
            resizeImage.get(i).setFitWidth(width);

        }
        try
        {

            //addThumbnails(resizeImage);//resized images
        } catch (Exception e)
        {
            showAlert("-" + e.toString());
        }
    }
    public void rootForControlsAndRowsColorChanger(VBox customs,VBox rowContainer,List<HBox> rows,HBox currentRow)
    {
        String[] colors = {
                "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);",  // Blue gradient
                "-fx-background-color: linear-gradient(to right, #ff9966, #ff5e62);",  // Warm orange-red
                "-fx-background-color: linear-gradient(to right, #56ab2f, #a8e063);",  // Green gradient
                "-fx-background-color: linear-gradient(to right, #7f00ff, #e100ff);"   // Purple gradient
        };

        String[] color = {
                "-fx-background-color: linear-gradient(to right, #ff9a9e, #fad0c4);",  // Soft pink
                "-fx-background-color: linear-gradient(to right, #ff758c, #ff7eb3);",  // Pinkish red
                "-fx-background-color: linear-gradient(to right, #43cea2, #185a9d);",  // Teal to dark blue
                "-fx-background-color: linear-gradient(to right, #ff6a00, #ee0979);"   // Fiery orange-red
        };

        String[] color1 = {
                "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);",  // Sky blue gradient
                "-fx-background-color: linear-gradient(to right, #f2709c, #ff9472);",  // Peachy pink
                "-fx-background-color: linear-gradient(to right, #f7971e, #ffd200);",  // Yellow-orange
                "-fx-background-color: linear-gradient(to right, #00b09b, #96c93d);"   // Emerald green
        };


        if(colorChangeTracker<4)
        {
            //changing colors at run time for customization
            customs.setStyle(colors[colorChangeTracker]);
            rowContainer.setStyle(color[colorChangeTracker]);
            root.setStyle(color1[colorChangeTracker]);
            rootForControlsAndRows.setStyle(color1[colorChangeTracker]);

        }else
        {
            //changing colors at run time for customization
            colorChangeTracker = 0;// to reset the index of the color when all colors are used
            customs.setStyle(colors[colorChangeTracker]);
            rowContainer.setStyle(color[colorChangeTracker]);
            root.setStyle(color1[colorChangeTracker]);
            rootForControlsAndRows.setStyle(color1[colorChangeTracker]);
        }

        colorChangeTracker = colorChangeTracker + 1;//incrementing tools

    }


    public static void main(String[] args)
    {

        launch();
    }
}

