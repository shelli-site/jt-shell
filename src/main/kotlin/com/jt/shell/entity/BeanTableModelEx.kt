package com.jt.shell.entity

import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.shell.table.TableModel


class BeanTableModelEx<T> : TableModel() {
    companion object {
        fun <T> builder(): BeanTableModelEx<T> {
            return BeanTableModelEx()
        }
    }


    private var data: List<BeanWrapper>? = null
    private var headers: ArrayList<String>? = null

    fun data(list: List<T>): BeanTableModelEx<T> {
        data = list.map { t -> BeanWrapperImpl(t as Any) }.toList()
        return this
    }

    fun clazz(clazz: Class<T>?): BeanTableModelEx<T> {
        headers = ArrayList()
        for (propertyName in BeanUtils.getPropertyDescriptors(clazz!!)) {
            if ("class" == propertyName.name) {
                continue
            }
            headers!!.add(propertyName.name)
        }
        return this
    }

    override fun getRowCount(): Int {
        return (data?.size ?: 0) + 1
    }

    override fun getColumnCount(): Int {
        return (headers?.size ?: 0)
    }

    override fun getValue(row: Int, column: Int): Any? {
        if (row == 0) {
            return headers?.get(column)
        }
        return data?.get(row - 1)?.getPropertyValue(headers!!.get(column))
    }
}



