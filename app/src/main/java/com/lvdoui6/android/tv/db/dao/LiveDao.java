package com.lvdoui6.android.tv.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.lvdoui6.android.tv.bean.Live;

@Dao
public abstract class LiveDao extends BaseDao<Live> {

    @Query("SELECT * FROM Live WHERE name = :name")
    public abstract Live find(String name);
}
