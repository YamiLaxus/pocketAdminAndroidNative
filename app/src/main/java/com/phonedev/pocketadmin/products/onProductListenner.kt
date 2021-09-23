package com.phonedev.pocketadmin.products

import com.phonedev.pocketadmin.entities.Product

interface onProductListenner {

    fun onClick(product: Product)
    fun onLongClick(product: Product)
}