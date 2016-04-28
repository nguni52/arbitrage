package arbitrage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nguni52 on 16/04/28.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Path {
    private String base;
    private Double rate;
}
