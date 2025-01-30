package net.myplayplanet.permission.service.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PermissionValue {

    NEUTRAL(0),
    GRANTED(1),
    DENIED(-1);

    private final int value;

}
