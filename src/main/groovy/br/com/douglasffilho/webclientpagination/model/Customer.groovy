package br.com.douglasffilho.webclientpagination.model

import groovy.transform.ToString

@ToString(
        includePackage = false,
        includeFields = true,
        includeNames = true
)
class Customer {

    String email
    String givenName
    String familyName

}
