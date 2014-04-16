package edu.upenn.cis455.youtube;

import java.io.File;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

/**
 * Class for the store DAO.
 * @author Zhishen Wen
 * @version Mar 29, 2013
 */
public class StoreDAO {
    /* static members */
    private static EntityStore store;
    private static Environment env;
    private static PrimaryIndex<String, Item> itemIndex;
    
    private static String path;
    
    /** sets the store path */
    public static void setStorePath(String newPath) {
        path = newPath;
     }
    
    /** sets up the store */
    public static void setup() {
        File dir = new File(path);
        dir.mkdirs();
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();
        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        env = new Environment(dir, envConfig);
        StoreDAO.store = new EntityStore(env, "EntityStore", storeConfig);
        itemIndex = store.getPrimaryIndex(String.class, Item.class);
    }
    
    /** shuts down the store */
    public static void shutdown() {
        store.close();
        env.close();
    }
    
    /** puts cache item in the store */
    public static void putItem(Item item) {
        itemIndex.put(item);
    }
    
    /** gets cache item from the store */
    public static Item getItem(String keyword) {
        return itemIndex.get(keyword);
    }
    
    /** deletes cache item from the store */
    public static void deleteItem(String keyword) {
        itemIndex.delete(keyword);
    }
    
    /** deletes all cache items from the store */
    public static void clearStore() {
        EntityCursor<Item> cursor = itemIndex.entities();
        for (Item item : cursor)
            cursor.delete();
        cursor.close();
    }

}
