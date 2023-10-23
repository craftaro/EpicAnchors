package com.craftaro.epicanchors.utils;

import com.craftaro.core.database.Data;
import com.craftaro.core.database.DataManager;
import com.craftaro.core.third_party.org.jooq.Queries;
import com.craftaro.core.third_party.org.jooq.Query;
import com.craftaro.core.third_party.org.jooq.Record1;
import com.craftaro.core.third_party.org.jooq.Result;
import com.craftaro.core.third_party.org.jooq.impl.DSL;
import com.craftaro.epicanchors.AnchorImpl;
import com.craftaro.epicanchors.EpicAnchors;
import com.craftaro.epicanchors.api.Anchor;
import com.craftaro.epicanchors.files.migration.AnchorMigration;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DataHelper {

    public static void exists(@NotNull String worldName, int x, int y, int z, @NotNull Callback<Boolean> callback) {
        DataManager dataManager = EpicAnchors.getPlugin(EpicAnchors.class).getDataManager();

        dataManager.getDatabaseConnector().connectDSL(dslContext -> {
            try {
                @NotNull Result<Record1<Object>> result = dslContext.select(DSL.field("id")).from(DSL.table(dataManager.getTablePrefix() + "anchors"))
                        .where(DSL.field("world_name").eq(worldName))
                        .and(DSL.field("x").eq(x))
                        .and(DSL.field("y").eq(y))
                        .and(DSL.field("z").eq(z)).fetch();

                callback.accept(null, result.size() > 0);
            } catch (Exception ex) {
                resolveCallback(callback, ex);
            }
        });
    }

    public static void getAnchors(@Nullable World world, @NotNull Callback<List<Anchor>> callback) {
        try {
            callback.accept(null, EpicAnchors.getPlugin(EpicAnchors.class).getDataManager().loadBatch(AnchorImpl.class, "anchors"));
        } catch (Exception ex) {
            resolveCallback(callback, ex);
        }
    }

    private static void resolveUpdateCallback(@Nullable UpdateCallback callback, @Nullable Exception ex) {
        if (callback != null) {
            callback.accept(ex);
        } else if (ex != null) {
            Utils.logException(EpicAnchors.getPlugin(EpicAnchors.class), ex, "SQL");
        }
    }

    private static void resolveCallback(@Nullable Callback<?> callback, @NotNull Exception ex) {
        if (callback != null) {
            callback.accept(ex, null);
        } else {
            Utils.logException(EpicAnchors.getPlugin(EpicAnchors.class), ex, "SQL");
        }
    }


    public static void migrateAnchor(List<AnchorMigration.LegacyAnchorEntry> anchorQueue, UpdateCallback callback) {
        DataManager dataManager = EpicAnchors.getPlugin(EpicAnchors.class).getDataManager();

        //recreate it with Jooq
        dataManager.getDatabaseConnector().connectDSL(dslContext -> {
            Connection connection = dslContext.configuration().connectionProvider().acquire();
            connection.setAutoCommit(false);
            try {
                List<Query> queries = new ArrayList<>();
                for (AnchorMigration.LegacyAnchorEntry entry : anchorQueue) {
                    queries.add(dslContext.insertInto(DSL.table(dataManager.getTablePrefix() + "anchors"))
                            .columns(
                                    DSL.field("world_name"),
                                    DSL.field("x"),
                                    DSL.field("y"),
                                    DSL.field("z"),
                                    DSL.field("ticks_left"))
                            .values(entry.worldName, entry.x, entry.y, entry.z, entry.ticksLeft));
                }
                dslContext.batch(queries).execute();
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                resolveUpdateCallback(callback, ex);
            } finally {
                connection.setAutoCommit(true);
                dslContext.configuration().connectionProvider().release(connection);
            }
        });
    }
}
