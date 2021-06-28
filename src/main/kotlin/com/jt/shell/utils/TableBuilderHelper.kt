package com.jt.shell.utils

import com.jt.shell.entity.BeanTableModelEx
import org.springframework.shell.table.*


object TableBuilderHelper {

    fun <T> designTableStyle(clazz: Class<T>, data: List<T>,
                             fullBorderStyle: BorderStyle = BorderStyle.fancy_double,
                             innerBorderStyle: BorderStyle = BorderStyle.fancy_light,
                             cellMatcher: CellMatcher = CellMatchers.table(),
                             aligner: Aligner = SimpleHorizontalAligner.center): Table {

        return TableBuilder(BeanTableModelEx.builder<T>().clazz(clazz).data(data))
                .addFullBorder(fullBorderStyle)
                .addInnerBorder(innerBorderStyle)
                .on(cellMatcher)
                .addAligner(aligner)
                .build()
    }

}