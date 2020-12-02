package life.jlu.osgi.packagetool.controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Adds the functionality to the help.fxml scene.
 *
 * @author Jannis Hochmuth
 */
public class HelpController {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML Node References -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private TreeView<CustomTreeItem> topic_tree;

    @FXML
    private ScrollPane topic_content_pane;

    @FXML
    private FlowPane topic_flow;

    @FXML
    private VBox topic_content;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Initialization -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void initialize() {
        initListener();
        populateTree();
    }

    private void initListener() {
        topic_tree.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {
                CustomTreeItem item = newValue.getValue();

                topic_content.getChildren().clear();
                topic_content.getChildren().addAll(item.getVboxContent());
            }
        });

        HBox.setHgrow(topic_content, Priority.ALWAYS);
        VBox.setVgrow(topic_content, Priority.ALWAYS);
    }

    private void populateTree() {
        TreeItem<CustomTreeItem> rootItem = new TreeItem<>( createRoot() );
        rootItem.setExpanded(true);

        topic_tree.setRoot(rootItem);
        topic_tree.setShowRoot(false);

        topic_content.getChildren().addAll( rootItem.getValue().getVboxContent() );


        TreeItem<CustomTreeItem> howTo = new TreeItem<>( howToSection() );
        rootItem.getChildren().add(howTo);
        howTo.setExpanded(true);

        TreeItem<CustomTreeItem> loadDistro = new TreeItem<>( loadDistroTopic() );
        loadDistro.setGraphic( new ImageView("/images/help_topic_16.png") );
        howTo.getChildren().add(loadDistro);

        TreeItem<CustomTreeItem> loadBundle = new TreeItem<>( loadProjectTopic() );
        loadBundle.setGraphic( new ImageView("/images/help_topic_16.png") );
        howTo.getChildren().add(loadBundle);
    }

    /**
     * Creates the CustomTreeItem for the TreeView root node with its content for the help box.
     * @return CustomTreeItem
     */
    private CustomTreeItem createRoot() {
        Label header = createCustomLabel(
                "Welcome to the help of the OSGi Package Tool",
                "help-h1", new Insets(0,0,15,0));

        Label subHeader = createCustomLabel(
                "Choose a topic you want to know more about from the menu to the left.",
                "help-p", new Insets(25,0,25,0));

        return new CustomTreeItem("Root", "[OPT] Help",
                header, new Separator(), subHeader);
    }

    private CustomTreeItem howToSection() {
        Label header = createCustomLabel(
                "How to use the OSGi Package Tool",
                "help-h1", new Insets(0,0,15,0));

        Label content = createCustomLabel(
                "This section deals with the individual topics related to how to "
              + "load the different JARs into the tool.",
                "help-p", new Insets(25,0,25,0));

        return new CustomTreeItem("howto", "How to use this application",
                header, new Separator(), content);
    }

    private CustomTreeItem loadDistroTopic() {
        Label header = createCustomLabel(
                "How to load the distro JAR",
                "help-h1", new Insets(0,0,15,0));

        Label subHeader = createCustomLabel(
                "This topic deals with the question how you load the distro JAR of your"
              + "OSGi container into the application",
                "help-h3", new Insets(25,0,25,0));
        subHeader.widthProperty().addListener(event ->
                System.out.println("Width of sub headline: "+subHeader.getWidth()+"   Word-Wrap:"+subHeader.isWrapText()));

        Label contentP01 = createCustomLabel(
                "There are two ways to load the distro JAR of your OSGi container into the tool:",
                "help-p", new Insets(0,0,10,0));

        Label contentP02 = createCustomLabel(
                "If there is access to an aQute Remote Agent, the distro JAR of the container can "
              + "be created via the menu and loaded into the tool. In this case the appropriate "
              + "access settings must first be set in the menu under File > Preferences. "
              + "Afterwards the distro JAR of the OSGi container can be created and loaded "
              + "directly into the tool by clicking Edit > Create&Load Distro.",
                "help-p", new Insets(0,0,10,25));

        ImageView image01 = createCustomImageView("/images/load_distro_01.png", 802, new Insets(25));

        Label contentP03 = createCustomLabel(
                "Otherwise the distro JAR must be created on the server of the OSGi Container "
              + "and then copied locally. The distro JAR can then be loaded by drag and drop "
              + "on the left side of the tool.",
                "help-p", new Insets(0,0,10,25));

        ImageView image02 = createCustomImageView("/images/load_distro_02.png", 802, new Insets(25));

        return new CustomTreeItem("loaddistro", "Load distro JAR",
                header, new Separator(), subHeader, contentP01, contentP02, image01, contentP03, image02);
    }

    private CustomTreeItem loadProjectTopic() {

        Label header = createCustomLabel(
                "How to use load your projects JAR",
                "help-h1", new Insets(0,0,15,0));

        Label subHeader = createCustomLabel(
                "This topic deals with the question how you load the OSGi bundle JAR of your "
              + "project into the application.",
                "help-h3", new Insets(25,0,25,0));


        Label contentP01 = createCustomLabel(
                "The project must be available as JAR with the correct bundle headers "
              + "(at least Bundle-SymbolicName) set in the MANIFEST.MF. This JAR can be loaded "
              + "simply by drag and drop on the right side of the tool.",
                "help-p", new Insets(0,0,10,0));

        ImageView image = createCustomImageView("/images/load_project_01.png", 802, new Insets(25));

        return new CustomTreeItem("loadproject", "Load project JAR",
                header,  subHeader, contentP01, image);
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML defined actions -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------------- */
    /* ----- Helper methods -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Creates a custom label with a styleclass and custom margin.
     * @param text
     * @return
     */
    private Label createCustomLabel(String text, String styleclass, Insets margin) {
        Label customLabel = new Label(text);
        customLabel.getStyleClass().add(styleclass);
        customLabel.setWrapText(true);
        customLabel.setPrefWidth(100);

        customLabel.prefWidthProperty().bind( topic_content_pane.widthProperty().subtract(100) );

        VBox.setMargin(customLabel, margin);

        return customLabel;
    }

    /**
     * Creates a custom label with a styleclass, custom margin and text wrap.
     * @param text
     * @return
     */
    private Label createCustomLabel(String text, String styleclass, Insets margin,
                                    boolean textWrap) {
        Label customLabel = new Label(text);
        customLabel.getStyleClass().add(styleclass);
        customLabel.setWrapText(textWrap);
        VBox.setMargin(customLabel, margin);

        return customLabel;
    }

    /**
     * Creates a custom label with a styleclass, custom margin, custom padding and text wrap.
     * @param text
     * @return
     */
    private Label createCustomLabel(String text, String styleclass, Insets margin, Insets padding,
                                    boolean textWrap) {
        Label customLabel = new Label(text);
        customLabel.getStyleClass().add(styleclass);
        customLabel.setWrapText(textWrap);
        customLabel.setPadding(padding);
        VBox.setMargin(customLabel, margin);

        return customLabel;
    }


    private ImageView createCustomImageView(String resource, double width, Insets margin) {
        ImageView image = new ImageView(resource);
        image.setPreserveRatio(true);
        image.setFitWidth(width);

        VBox.setMargin(image, margin);

        return image;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Helper classes -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Custom class for the TreeView items to hold the help box content of each node.
     * @author Jannis Hochmuth
     */
    private static class CustomTreeItem {
        private final SimpleStringProperty name  = new SimpleStringProperty();
        private final SimpleStringProperty title = new SimpleStringProperty();
        private final List<Node> vboxContent     = new ArrayList<>();

        /**
         *
         * @param name
         * @param title
         * @param vboxContent
         */
        CustomTreeItem(String name, String title, Node... vboxContent) {
            this.name.setValue(name);
            this.title.setValue(title);

            this.vboxContent.addAll(Arrays.asList(vboxContent));
        }


        /* ----------------------------------------------------------------------------------------- */
        /* ----- Getter & Setter ------------------------------------------------------------------- */
        /* ----------------------------------------------------------------------------------------- */
        public String getName()                  { return name.get(); }
        public void setName(String name)         { this.name.setValue(name); }
        public StringProperty getNameProperty()  { return name; }

        public String getTitle()                 { return title.get(); }
        public void setTitle(String title)       { this.title.setValue(title); }
        public StringProperty getTitleProperty() { return title; }

        public List<Node> getVboxContent() { return this.vboxContent; }


        /* ----------------------------------------------------------------------------------------- */
        /* ----- Object Methods -------------------------------------------------------------------- */
        /* ----------------------------------------------------------------------------------------- */
        @Override
        public String toString() { return this.getTitle(); }
    }
}
