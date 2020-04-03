package com.kelab.experiment.dal.redis.callback;

public interface OneCacheCallback<K, V> {

    V queryFromDB(K missKey);

}
