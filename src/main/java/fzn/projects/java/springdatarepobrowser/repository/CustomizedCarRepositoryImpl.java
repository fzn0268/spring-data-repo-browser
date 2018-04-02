package fzn.projects.java.springdatarepobrowser.repository;

import fzn.projects.java.springdatarepobrowser.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.core.mapping.RedisPersistentEntity;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class CustomizedCarRepositoryImpl implements CustomizedCarRepository {

    @Autowired
    private RedisTemplate<byte[], byte[]> redisTemplate;

    @Autowired
    private RedisKeyValueAdapter redisKeyValueAdapter;

    @Autowired
    private RedisMappingContext mappingContext;

    @Override
    public void deleteAll(Iterable<Car> entities) {
        SetOperations<byte[], byte[]> opsForSet = redisTemplate.opsForSet();
        RedisPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(Car.class);
        String keySpace = persistentEntity.getKeySpace();
        byte[] binKeySpace = redisKeyValueAdapter.toBytes(keySpace);
        Set<byte[]> keyToDelSet = new HashSet<>();
        Set<byte[]> binIdSet = new HashSet<>();
        Map<byte[], byte[]> binIdToIndexHelperKeyMap = new HashMap<>();
        Map<byte[], Set<byte[]>> binIdToIndexKeysMap = new HashMap<>();
        Map<byte[], DataType> indexKeyToDataTypeMap = new HashMap<>();
        for (Car car : entities) {
            String id = (String) persistentEntity.getIdentifierAccessor(car).getIdentifier();
            byte[] keyToDel = redisKeyValueAdapter.createKey(keySpace, id);
            keyToDelSet.add(keyToDel);
            byte[] binId = redisKeyValueAdapter.toBytes(id);
            binIdSet.add(binId);
            // store about index keys
            byte[] indexHelperKey = ByteUtils.concatAll(redisKeyValueAdapter.toBytes(keySpace + ":"), binId,
                    redisKeyValueAdapter.toBytes(":idx"));
            binIdToIndexHelperKeyMap.put(binId, indexHelperKey);
            Set<byte[]> indexKeySet = opsForSet.members(indexHelperKey);
            binIdToIndexKeysMap.put(binId, indexKeySet);
            for (byte[] indexKey : indexKeySet) {
                indexKeyToDataTypeMap.put(indexKey, redisTemplate.type(indexKey));
            }
        }

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(keyToDelSet.toArray(new byte[keyToDelSet.size()][]));
                connection.sRem(binKeySpace, binIdSet.toArray(new byte[binIdSet.size()][]));
                // delete about index keys
                for (Map.Entry<byte[], byte[]> binIdToIndexHelperKeyEntry : binIdToIndexHelperKeyMap.entrySet()) {
                    byte[] binId = binIdToIndexHelperKeyEntry.getKey();
                    byte[] indexHelperKey = binIdToIndexHelperKeyEntry.getValue();
                    Set<byte[]> indexKeySet = binIdToIndexKeysMap.get(binId);
                    for (byte[] indexKey : indexKeySet) {
                        DataType dataType = indexKeyToDataTypeMap.get(indexKey);
                        if (DataType.ZSET.equals(dataType)) {
                            connection.zRem(indexKey, binId);
                        } else {
                            connection.sRem(indexKey, binId);
                        }
                    }
                    connection.del(indexHelperKey);
                }
                return null;
            }
        });
    }
}
