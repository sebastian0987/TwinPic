package com.durrutia.twinpic.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 20-Oct-16.
 */
@Slf4j
@Builder
public class Pic extends BaseModel {

    @Getter
    Long id;

    @Getter
    String deviceId;

    @Getter
    Long date;

    @Getter
    Double latitude;

    @Getter
    Double longitude;

    @Getter
    Integer positive;

    @Getter
    Integer negative;

    @Getter
    Integer warning;


}
