package com.jt.shell.utils

import com.jt.shell.entity.BeanTableModelEx
import org.springframework.shell.table.*

/**
 * 打印表格数据
 */
inline fun <reified T> designTableStyle(
    data: List<T>,
    fullBorderStyle: BorderStyle = BorderStyle.fancy_heavy,
    innerBorderStyle: BorderStyle = BorderStyle.fancy_light,
    cellMatcher: CellMatcher = CellMatchers.table(),
    aligner: Aligner = SimpleHorizontalAligner.center
): Table {

    return TableBuilder(BeanTableModelEx.builder<T>().clazz(T::class.java).data(data))
        .addFullBorder(fullBorderStyle)
        .addInnerBorder(innerBorderStyle)
        .on(cellMatcher)
        .addAligner(aligner)
        .build()
}