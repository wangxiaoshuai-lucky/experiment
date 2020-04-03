package com.kelab.experiment.dal.redis.callback;

import java.util.List;
import java.util.Map;

public interface ListCacheCallback<K, V> {

    Map<K, V> queryFromDB(List<K> missKeyList);

}
