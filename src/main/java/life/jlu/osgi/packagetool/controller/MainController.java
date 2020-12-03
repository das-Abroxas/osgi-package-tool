package life.jlu.osgi.packagetool.controller;

import aQute.bnd.main.bnd;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import life.jlu.osgi.packagetool.application.AppPreferences;
import life.jlu.osgi.packagetool.application.Main;
import life.jlu.osgi.packagetool.exceptions.ExitSecurityManager;
import life.jlu.osgi.packagetool.exceptions.InvalidDistroJarException;
import life.jlu.osgi.packagetool.exceptions.InvalidBundleException;
import life.jlu.osgi.packagetool.model.Bundle;
import life.jlu.osgi.packagetool.model.ExportPackage;
import life.jlu.osgi.packagetool.model.ImportPackage;
import life.jlu.osgi.packagetool.model.Package;
import life.jlu.osgi.packagetool.util.DialogUtil;
import life.jlu.osgi.packagetool.util.ExecutorUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * Adds the functionality to the main.fxml scene.
 *
 * @author Jannis Hochmuth
 */
public class MainController {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML Node References -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private GridPane main_pane;
    @FXML
    private StackPane container_pane;
    @FXML
    private StackPane bundle_pane;

    @FXML
    private MenuBar header_menu_bar;

    @FXML
    private VBox container_box;
    @FXML
    private VBox bundle_box;

    @FXML
    private TextField container_search;
    @FXML
    private TextField bundle_search;

    @FXML
    private Label container_label;
    @FXML
    private Label bundle_label;

    @FXML
    private ListView<ExportPackage> container_list;
    @FXML
    private ListView<ImportPackage> bundle_list;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Global Variables ------------------------------------------------------------------ */
    /* ----------------------------------------------------------------------------------------- */
    private final static ExitSecurityManager secManager = new ExitSecurityManager();
    private final HostServices hostServices;

    private Bundle container_bundle;
    private Bundle project_bundle;

    private ObservableList<ExportPackage> container_pkgs;
    private ObservableList<ImportPackage> project_pkgs;

    private FilteredList<ExportPackage> filtered_container_pkgs;
    private FilteredList<ImportPackage> filtered_project_pkgs;

    private BooleanProperty container_loaded = new SimpleBooleanProperty(false);
    private BooleanProperty project_loaded = new SimpleBooleanProperty(false);

    private Stage package_meta;
    private PackageMetaController package_meta_controller;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructor ----------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Constructor of class MainController.
     * @param hostServices Provides HostServices access for an Application
     */
    public MainController(HostServices hostServices) {
        this.hostServices = hostServices ;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Initialization -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        initBindings();

        initContainerFilter();
        initBundleFilter();

        initBundleListCellFactory();
    }

    private void initBindings() {
        // Container Pane
        container_label.visibleProperty().bind( container_loaded.not() );
        container_label.managedProperty().bind( container_label.visibleProperty() );

        container_search.disableProperty().bind( container_loaded.not() );

        container_list.visibleProperty().bind( container_loaded );
        container_list.managedProperty().bind( container_list.visibleProperty() );

        // Project Bundle Pane
        bundle_label.visibleProperty().bind( project_loaded.not() );
        bundle_label.managedProperty().bind( bundle_label.visibleProperty() );

        bundle_search.disableProperty().bind( project_loaded.not() );

        bundle_list.visibleProperty().bind(project_loaded);
        bundle_list.managedProperty().bind( bundle_list.visibleProperty() );
    }

    private void initContainerFilter() {
        container_pkgs = FXCollections.observableArrayList();
        filtered_container_pkgs = new FilteredList<>(container_pkgs, s -> true);

        container_search.textProperty().addListener(obs -> {
            String filter = container_search.getText();

            if (filter == null || filter.length() == 0)
                filtered_container_pkgs.setPredicate(s -> true);
            else
                filtered_container_pkgs.setPredicate(s -> s.getFqn().contains(filter));
        });

        container_list.setItems(filtered_container_pkgs);
    }

    private void initBundleFilter() {
        project_pkgs = FXCollections.observableArrayList();
        filtered_project_pkgs = new FilteredList<>(project_pkgs, s -> true);

        bundle_search.textProperty().addListener(obs -> {
            String filter = bundle_search.getText();

            if (filter == null || filter.length() == 0)
                filtered_project_pkgs.setPredicate(s -> true);
            else
                filtered_project_pkgs.setPredicate(s -> s.getFqn().contains(filter));
        });

        bundle_list.setItems(filtered_project_pkgs);
    }

    /**
     * Sets a custom CellFactory for the projects ListView which sets a style class
     *   depending on the ImportPackage attributes:
     *     - Resolved:     green background
     *     - Optional:     yellow background
     *     - Not resolved: red background
     */
    private void initBundleListCellFactory() {

        bundle_list.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ImportPackage> call(ListView<ImportPackage> importPackageListView) {

                return new ListCell<>() {
                    @Override
                    protected void updateItem(ImportPackage iPkg, boolean empty) {
                        super.updateItem(iPkg, empty);

                        getStyleClass().removeAll("resolved", "optional-not-resolved",
                                "dynamic-not-resolved", "not-resolved");

                        if (empty) {
                            setText(null);
                            setStyle("");
                            return;
                        }

                        setText(iPkg.toString());

                        if (iPkg.isResolved())
                            getStyleClass().add("resolved");
                        else if (iPkg.isOptional())
                            getStyleClass().add("optional-not-resolved");
                        else if (iPkg.isDynamic())
                            getStyleClass().add("dynamic-not-resolved");
                        else
                            getStyleClass().add("not-resolved");
                    }
                };
            }
        });
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Drag and Drop Actions ------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void onContainerDragOver(DragEvent event) {
        if (event.getGestureSource() != container_pane && event.getDragboard().hasFiles())
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

        container_label.getStyleClass().add("drop_label_dragover");
        event.consume();
    }

    @FXML
    private void onContainerDragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (!db.hasFiles() || db.getFiles().size() > 1) {
            DialogUtil.showErrorDialog("DragNDrop Error", "Only single JAR allowed!");
            event.consume();
            return;

        } else if (!db.getFiles().get(0).getName().endsWith(".jar")) {
            DialogUtil.showErrorDialog("Wrong file format!", "Only .jar files allowed.");
            event.consume();
            return;
        }

        boolean success = loadContainerJar( db.getFiles().get(0) );

        event.setDropCompleted(success);
        event.consume();

        if (success && project_loaded.getValue())
            resolveBundlePackages();
    }

    @FXML
    private void onContainerDragExit() {
        container_label.getStyleClass().clear();
        container_label.getStyleClass().add("drop_label");
    }


    @FXML
    private void onBundleDragOver(DragEvent event)  {
        if (event.getGestureSource() != container_pane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        bundle_label.getStyleClass().add("drop_label_dragover");
        event.consume();
    }

    @FXML
    private void onBundleDragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (!db.hasFiles() || db.getFiles().size() > 1) {
            DialogUtil.showErrorDialog("DragNDrop Error", "Only single JAR allowed!");
            event.consume();
            return;

        } else if (!db.getFiles().get(0).getName().endsWith(".jar")) {
            DialogUtil.showErrorDialog("Wrong file format!", "Only .jar files allowed.");
            event.consume();
            return;
        }

        boolean success = loadBundleJar( db.getFiles().get(0) );

        event.setDropCompleted(success);
        event.consume();

        if (success && container_loaded.getValue())
            resolveBundlePackages();
    }

    @FXML
    private void onBundleDragExit() {
        bundle_label.getStyleClass().clear();
        bundle_label.getStyleClass().add("drop_label");
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- MenuItem Actions ------------------------------------------------------------------ */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void showPreferences() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/preferences.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 310);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("OSGi Package Tool Preferences");
            stage.getIcons().add(new Image("/images/OSGi-logo.png"));
            stage.setMinWidth(600);
            stage.setMaxWidth(1920);
            stage.setMinHeight(450);
            stage.setMaxHeight(1080);

            stage.initModality( Modality.APPLICATION_MODAL );
            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createDistro() {

        CreateDistroTask bndTask = new CreateDistroTask();

        OverlayTask task = new OverlayTask(container_pane, bndTask,
                Collections.singletonList(header_menu_bar),
                AppPreferences.getIntegerPreference("task_timeout"));

        ExecutorService executor = ExecutorUtil.getSingleThreadDaemonExecutor();
        executor.execute(task);
        executor.shutdown();
    }

    @FXML
    private void showGraph() {
        DialogUtil.showErrorDialog("Not implemented.", "This feature still needs to be implemented.");
    }

    @FXML
    private void showHelp() {

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/help.fxml"));
            Parent root = loader.load();

            double height = Screen.getPrimary().getVisualBounds().getHeight() * 0.9;
            double width = Screen.getPrimary().getVisualBounds().getWidth() * 0.9;

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("OSGi Package Tool Help");
            stage.getIcons().add(new Image("/images/OSGi-logo.png"));
            stage.setMinWidth(600);
            stage.setMaxWidth(1920);
            stage.setMinHeight(600);
            stage.setMaxHeight(1920);
            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAbout() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("bundle.about", Locale.ENGLISH);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/about.fxml"), bundle);
            loader.setControllerFactory(new HostServicesControllerFactory(hostServices));

            Parent root = loader.load();
            Scene scene = new Scene(root, 300, 300);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("OSGi Package Tool About");
            stage.getIcons().add(new Image("/images/OSGi-logo.png"));
            stage.setResizable(false);
            stage.setMinWidth(400);
            stage.setMaxWidth(400);
            stage.setMinHeight(200);
            stage.setMaxHeight(200);
            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitApplication() {
        System.setSecurityManager(null);  // Disable catch System.exit()
        Platform.exit();                  // Exit application
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- ListView Actions ------------------------------------------------------------------ */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void showPackageMetadata(MouseEvent event) {

        if (bundle_list.getSelectionModel().getSelectedItem() != null &&
            event.getClickCount() == 2)
        {
            ImportPackage iPkg = bundle_list.getSelectionModel().getSelectedItem();

            if (package_meta == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/package.fxml"));
                    Parent root = loader.load();
                    package_meta_controller = loader.getController();

                    Scene scene = new Scene(root, 800, 250);
                    scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

                    package_meta = new Stage();
                    package_meta.setTitle("OSGi Package Metadata");
                    package_meta.getIcons().add(new Image("/images/OSGi-logo.png"));
                    package_meta.setMinWidth(800);
                    package_meta.setMaxWidth(1920);
                    package_meta.setMinHeight(250);
                    package_meta.setMaxHeight(1080);
                    package_meta.setScene(scene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            package_meta.show();
            package_meta_controller.update( iPkg );
        }
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- OSGi Bundle loading and validation methods ---------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private boolean loadContainerJar(File jarFile) {

        LoadBundleTask loader = new LoadBundleTask(jarFile, true);

        OverlayTask task = new OverlayTask(container_pane, loader,
                Collections.singletonList(header_menu_bar),
                AppPreferences.getIntegerPreference("task_timeout"));

        ExecutorService executor = ExecutorUtil.getSingleThreadDaemonExecutor();
        executor.execute(task);
        executor.shutdown();

        return true;
    }

    private void validateContainerBundle(Bundle bundle) throws InvalidDistroJarException {

        if (bundle.getBundleSymbolicName() == null)
            throw new InvalidDistroJarException("OSGi Container Distro has no Bundle-SymbolicName.");

        else if (bundle.getImports() != null && !bundle.getImports().isEmpty())
            throw new InvalidDistroJarException("OSGi Container Distro contains imports.");

        else if (bundle.getDynamicImports() != null && !bundle.getDynamicImports().isEmpty())
            throw new InvalidDistroJarException("OSGi Container Distro contains dynamic imports.");

        else if (bundle.getExports() == null || bundle.getExports().isEmpty())
            throw new InvalidDistroJarException("OSGi Container Distro does not contain exports.");
    }

    private void updateContainerJar(Bundle bundle) {

        try {
            validateContainerBundle(bundle);

            container_bundle = bundle;

            container_pkgs.clear();
            container_pkgs.addAll(container_bundle.getExports());

            FXCollections.sort(container_pkgs, Comparator.comparing(Package::getFqn));

            container_loaded.setValue(true);

            if (project_loaded.get())
                resolveBundlePackages();

        } catch (InvalidDistroJarException idje) {
            DialogUtil.createExceptionDialog(idje, "Invalid OSGi Bundle", idje.getMessage());
        }
    }


    private boolean loadBundleJar(File jarFile) {

        LoadBundleTask loader = new LoadBundleTask(jarFile, false);

        OverlayTask task = new OverlayTask(bundle_pane, loader,
                Collections.singletonList(header_menu_bar),
                AppPreferences.getIntegerPreference("task_timeout"));

        ExecutorService executor = ExecutorUtil.getSingleThreadDaemonExecutor();
        executor.execute(task);
        executor.shutdown();

        return true;
    }

    private void validateProjectBundle(Bundle bundle) throws InvalidBundleException {

        if (bundle.getBundleSymbolicName() == null)
            throw new InvalidBundleException("Bundle has no Bundle-SymbolicName.");
    }

    private void updateBundleJar(Bundle bundle) {

        try {
            validateProjectBundle(bundle);

            project_bundle = bundle;

            project_pkgs.clear();
            project_pkgs.addAll(project_bundle.getImports());

            if (AppPreferences.getBooleanPreference("dynamic_include"))
                project_pkgs.addAll(project_bundle.getDynamicImports());

            FXCollections.sort(project_pkgs, Comparator.comparing(Package::getFqn));

            project_loaded.setValue(true);

            if (container_loaded.getValue())
                resolveBundlePackages();

        } catch (InvalidBundleException ibe) {
            DialogUtil.createExceptionDialog(ibe, "Invalid OSGi Bundle", ibe.getMessage());
        }
    }


    private void resolveBundlePackages() {
        // Resolve against exported packages from container
        project_pkgs.forEach(ip -> ip.resolve(container_bundle) );

        // Resolve against exported packages from project
        project_pkgs.stream()
                .filter(ip -> !ip.isResolved())
                .forEach(ip -> ip.resolve(project_bundle) );

        // Visual update on ListView
        bundle_list.refresh();
    }

    private boolean jarHasImports(Analyzer analyzer) {
        return analyzer.getImports().size() > 0;
    }

    private boolean bundleHasImports(Bundle bundle) {
        return bundle.getImports() != null && !bundle.getImports().isEmpty();
    }

    private boolean jarHasExports(Analyzer analyzer) {
        return analyzer.getExports().size() > 0;
    }

    private boolean bundleHasExports(Bundle bundle) {
        return bundle.getExports() != null && !bundle.getExports().isEmpty();
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Reset Application ----------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void reset() {
        System.setSecurityManager(null);  // Disable catch System.exit()

        // Reset container
        container_bundle = null;
        container_pkgs.clear();
        container_loaded.setValue( false );
        container_list.refresh();

        // Reset project
        project_bundle = null;
        project_pkgs.clear();
        project_loaded.set( false );
        bundle_list.refresh();
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- JavaFX Tasks ---------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private class OverlayTask extends Task<Void> {

        private final StackPane blocked;
        private final Task<?> task;
        private final List<Node> disabled;
        private final long timeout;

        private StackPane overlay;

        public OverlayTask(StackPane blocked, Task<?> task, List<Node> disabled, long timeout) {
            this.blocked = blocked;
            this.task = task;
            this.disabled = disabled;
            this.timeout = timeout;
        }

        @Override
        protected Void call() throws Exception {

            ProgressIndicator pi = new ProgressIndicator();

            Label piLabel = new Label();
            piLabel.textProperty().bind( task.messageProperty() );

            Button cancel = new Button();
            cancel.setPickOnBounds(true); // make sure transparent parts of the button register clicks too
            cancel.setAlignment(Pos.CENTER);
            cancel.getStyleClass().add("invisible");
            cancel.setGraphic( new ImageView("/images/cancel.png"));
            cancel.setOnAction(event -> cancel());

            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: #FFFFFF;");
            box.setSpacing(10);
            box.setMaxHeight(150);
            box.setMaxWidth(200);
            box.getChildren().addAll(pi, piLabel, cancel);

            overlay = new StackPane();
            StackPane.setMargin(overlay, new Insets(5,5,5,5));
            overlay.setAlignment(Pos.CENTER);
            overlay.setStyle("-fx-background-color: rgba(160, 160, 160, 0.7)");
            overlay.getChildren().addAll(box);

            // Disable menu and set overlay
            Platform.runLater(() -> {
                disabled.forEach( n -> n.setDisable(true) );
                blocked.getChildren().add(overlay);
            });

            if (timeout == 0) {
                task.run();
            } else {
                ExecutorService executor = ExecutorUtil.getSingleThreadDaemonExecutor();
                Future<?> timer = executor.submit(task);
                timer.get(timeout, TimeUnit.SECONDS);
                executor.shutdown();
            }

            return null;
        }

        @Override
        protected void succeeded() {
            System.out.println("Overlay Task succeeded.");

            Platform.runLater(() -> {
                disabled.forEach(n -> n.setDisable(false));
                blocked.getChildren().remove(overlay);
            });
        }

        @Override
        protected void cancelled() {
            System.out.println("Overlay Task cancelled.");
            task.cancel();

            Platform.runLater(() -> {
                disabled.forEach(n -> n.setDisable(false));
                blocked.getChildren().remove(overlay);
                DialogUtil.showInformationDialog("Task cancelled.", "Task was terminated by user.");
            });
        }

        @Override
        protected void failed() {
            System.out.println("Overlay Task failed.");
            task.cancel();

            Platform.runLater(() -> {
                DialogUtil.showErrorDialog("Timeout",
                        "Task took too long to finish and was terminated.");

                disabled.forEach(n -> n.setDisable(false));
                blocked.getChildren().remove(overlay);
            });
        }
    }

    private class LoadBundleTask extends Task<Bundle> {

        private final File file;
        private final boolean container;

        public LoadBundleTask(File file, boolean container) {
            this.file = file;
            this.container = container;
        }

        @Override
        protected Bundle call() throws Exception {

            updateMessage("Loading Bundle ...");

            Jar jar           = new Jar(file);
            Analyzer analyzer = new Analyzer(jar);
            analyzer.calcManifest();

            if (analyzer.getBundleSymbolicName() == null)
                throw new Exception("JAR is not a valid OSGi Bundle.");

            List<ExportPackage> exports = analyzer.getExports().entrySet().stream()
                    .map(e -> new ExportPackage(e.getKey().toString(), e.getValue()))
                    .collect(Collectors.toList());

            List<ImportPackage> imports = analyzer.getImports()
                    .entrySet()
                    .stream()
                    .map(e -> new ImportPackage(e.getKey().toString(), e.getValue()))
                    .collect(Collectors.toList());

            List<ImportPackage> dynamic = analyzer.getDynamicImportPackage()
                    .entrySet()
                    .stream()
                    .map(e -> {
                        e.getValue().put("resolution:", "dynamic");
                        return new ImportPackage(e.getKey(), e.getValue());
                    }).collect(Collectors.toList());

            return new Bundle(
                    analyzer.getBundleName(),
                    analyzer.getBundleSymbolicName().toString(),
                    analyzer.getBundleVersion(),
                    analyzer.getBundleDocURL(),
                    exports, imports, dynamic);
        }

        @Override
        protected void succeeded() {
            if (container)
                updateContainerJar( getValue() );
            else
                updateBundleJar( getValue() );
        }
    }

    private class CreateDistroTask extends Task<File> {

        private final bnd bndtools = new bnd();
        private final String stamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date());
        private final String jar   = String.format("liferay-osgi-distro_%s.jar", stamp);

        private final Path outputDir = Paths.get(AppPreferences.getStringPreference("container_path"));
        private final String jarPath = Paths.get(outputDir.toString(), jar).toString();

        private final String[] args = new String[] {
                "remote",
                "-h", AppPreferences.getStringPreference("container_host"),
                "-p", AppPreferences.getIntegerPreference("container_port").toString(),
                "distro",
                "-o", jarPath,             // Output file
                "liferay-osgi-container",  // Bundle-SymbolicName
                stamp.substring(0,8)       // Bundle-Version: yyyyMMdd
        };

        @Override
        protected File call() throws Exception {

            updateMessage("Creating Container Distro ...");

            System.setSecurityManager(secManager);  // Enable catch System.exit()
            bndtools.start(args);
            System.setSecurityManager(null);  // Disable catch System.exit()

            return new File(jarPath);
        }

        @Override
        protected void succeeded() {
            updateMessage("Loading Container Distro ...");
            loadContainerJar( getValue() );
        }

        @Override
        protected void failed() {
            Platform.runLater(() ->
                    DialogUtil.showErrorDialog("Creating Distro failed",
                            bndtools.getErrors().get( bndtools.getErrors().size()-1 )));
        }
    }
}
