package com.craftaro.epicanchors.files;

import com.craftaro.core.database.DatabaseConnector;
import com.craftaro.epicanchors.AnchorImpl;
import com.craftaro.epicanchors.api.Anchor;
import com.craftaro.epicanchors.files.migration.AnchorMigration;
import com.craftaro.epicanchors.utils.Callback;
import com.craftaro.epicanchors.utils.UpdateCallback;
import com.craftaro.epicanchors.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataManager  {



//    public void updateAnchors(Collection<Anchor> anchors, UpdateCallback callback) {
//        this.databaseConnector.connect((con) -> {
//            con.setAutoCommit(false);
//
//            SQLException err = null;
//
//            for (Anchor anchor : anchors) {
//                try (PreparedStatement ps = con.prepareStatement("UPDATE " + this.anchorTable +
//                        " SET ticks_left =? WHERE id =?;")) {
//                    ps.setInt(1, anchor.getTicksLeft());
//                    ps.setInt(2, anchor.getDbId());
//
//                    ps.executeUpdate();
//                } catch (SQLException ex) {
//                    err = ex;
//                    break;
//                }
//            }
//
//            if (err == null) {
//                con.commit();
//
//                resolveUpdateCallback(callback, null);
//            } else {
//                con.rollback();
//
//                resolveUpdateCallback(callback, err);
//            }
//
//            con.setAutoCommit(true);
//        });
//    }
//
//    public void deleteAnchorAsync(Anchor anchor) {
//        deleteAnchorAsync(anchor, null);
//    }
//
//    public void deleteAnchorAsync(Anchor anchor, UpdateCallback callback) {
//        this.thread.execute(() ->
//                this.databaseConnector.connect((con) -> {
//                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + this.anchorTable +
//                            " WHERE id =?;")) {
//                        ps.setInt(1, anchor.getDbId());
//
//                        ps.executeUpdate();
//
//                        resolveUpdateCallback(callback, null);
//                    } catch (Exception ex) {
//                        resolveUpdateCallback(callback, ex);
//                    }
//                })
//        );
//    }
//
//    public static String getTableName(String prefix, String name) {
//        String result = prefix + name;
//
//        if (!result.matches("[a-z0-9_]+")) {
//            throw new IllegalStateException("The generated table name '" + result + "' contains invalid characters");
//        }
//
//        return result;
//    }
}
