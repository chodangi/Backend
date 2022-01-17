package MCcrew.Coinportal.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorException  extends Exception {
    private ErrorCode status;
}
