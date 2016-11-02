package com.durrutia.twinpic.domain;

import com.durrutia.twinpic.Database;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 20-Oct-16.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        database = Database.class,
        cachingEnabled = true,
        orderedCursorLookUp = true, // https://github.com/Raizlabs/DBFlow/blob/develop/usage2/Retrieval.md#faster-retrieval
        cacheSize = Database.CACHE_SIZE
)
public class Pic extends BaseModel {

    /**
     * Identificador unico
     */
    @Getter
    @PrimaryKey(autoincrement = true)
    Long id;

    /**
     * Identificador del dispositivo
     */
    @Getter
    String deviceId;

    /**
     * Fecha de la foto
     */
    @Getter
    Long date;

    /**
     * Latitud
     */
    @Getter
    Double latitude;

    /**
     * Longitud
     */
    @Getter
    Double longitude;

    /**
     * Numero de likes
     */
    @Getter
    Integer positive;

    /**
     * Numero de dis-likes
     */
    @Getter
    Integer negative;

    /**
     * Numero de warnings
     */
    @Getter
    Integer warning;

}
