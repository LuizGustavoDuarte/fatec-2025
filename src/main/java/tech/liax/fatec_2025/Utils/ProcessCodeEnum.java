package tech.liax.fatec_2025.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.liax.fatec_2025.Exceptions.InvalidProcessCodeException;

@Getter
@AllArgsConstructor
public enum ProcessCodeEnum {
    FATEC_STAMP(1),
    LIAX_STAMP(2),
    OTHER(3);

    private final int code;

    public static ProcessCodeEnum fromCode(int code) {
        for (ProcessCodeEnum value : ProcessCodeEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new InvalidProcessCodeException("Código de processo inválido: " + code);
    }
}
