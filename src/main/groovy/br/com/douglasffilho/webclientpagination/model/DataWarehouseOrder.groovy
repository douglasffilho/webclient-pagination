package br.com.douglasffilho.webclientpagination.model

import groovy.transform.ToString

@ToString(
        includePackage = false,
        includeFields = true,
        includeNames = true
)
class DataWarehouseOrder {

    Long id
    Organization organization
    Customer customer

}
