package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.person.Person

data class Author(
    override val firstName: FirstName,
    override val lastName: LastName,
) : Person
