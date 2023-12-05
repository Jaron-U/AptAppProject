import java.sql.*;

public class DT_AptAppManager {

    private static DT_AptAppManager instance; // Singleton pattern

    public static DT_AptAppManager getInstance() {
        if (instance == null) {
            instance = new DT_AptAppManager();
        }
        return instance;
    }
    // Main components of this application

    private Connection connection;

    public Connection getDBConnection() {
        return connection;
    }

    private DT_DataAdaptor dataAccess;

    private User currentUser = null;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private DT_MainScreen mainScreen = null;
    private DT_RegisterScreen regScreen = new DT_RegisterScreen();
    private DT_AptListScreen aptList = new DT_AptListScreen();
    private DT_PostScreen postingScreen = new DT_PostScreen();

    private DT_AptDetailScreen aptDetailScreen = new DT_AptDetailScreen();

    public DT_AptDetailScreen getAptDetailScreen() {
        return aptDetailScreen;
    }

    public DT_PostScreen getPostingScreen() {
        return postingScreen;
    }

    public DT_AptListScreen getAptList() {
        return aptList;
    }

    public DT_RegisterScreen getRegScreen() {
        return regScreen;
    }

    private DT_PostScreen newPostScreen = new DT_PostScreen();

    public DT_MainScreen getMainScreen() {
        return mainScreen;
    }

    public DT_PostScreen getNewPostScreen() {
        return newPostScreen;
    }

    public DT_LoginScreen loginScreen = new DT_LoginScreen();

    public DT_LoginScreen getLoginScreen() {
        return loginScreen;
    }

    private DT_LoginController loginScreenCtrl = new DT_LoginController();
    private DT_PostingController postingCtrl;

    private DT_AptListController aptListController;
    private DT_WishListController wishListController;

    public DT_WishListController getWishListController() {
        return wishListController;
    }

    public DT_AptListController getAptListController() {
        return aptListController;
    }

    public DT_PostingController getPostingCtrl() {
        return postingCtrl;
    }

    public DT_LoginController getLoginScreenCtrl() {
        return loginScreenCtrl;
    }

    public DT_DataAdaptor getDataAccess() {
        return dataAccess;
    }

    private DT_WishListScreen wishListScreen = null;

    public DT_WishListScreen getWishListScreen() {
        return wishListScreen;
    }

    private DT_AptDetailController aptDetailController = null;

    public DT_AptDetailController getAptDetailController() {
        return aptDetailController;
    }

    private DT_AptAppManager() {
        dataAccess = new DT_DataAdaptor();
        dataAccess.Conn();

        mainScreen = new DT_MainScreen();
        // create SQLite database connection here!
        wishListScreen = new DT_WishListScreen();
        aptDetailScreen = new DT_AptDetailScreen();

        loginScreenCtrl = new DT_LoginController();
        postingCtrl = new DT_PostingController(newPostScreen);
        aptListController = new DT_AptListController(aptList, dataAccess);
        wishListController = new DT_WishListController(wishListScreen, dataAccess);
        aptDetailController = new DT_AptDetailController(aptDetailScreen, dataAccess);
    }
}
