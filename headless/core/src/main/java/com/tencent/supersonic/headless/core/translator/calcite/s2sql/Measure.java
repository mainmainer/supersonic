package com.tencent.supersonic.headless.core.translator.calcite.s2sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Measure {

    private String name;

    //sum max min avg count distinct
    private String agg;

    private String expr;

    private String constraint;

    private String alias;

    private String createMetric;
}
