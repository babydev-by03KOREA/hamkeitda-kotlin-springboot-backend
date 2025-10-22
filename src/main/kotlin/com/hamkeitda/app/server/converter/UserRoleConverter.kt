package com.hamkeitda.app.server.converter

import com.hamkeitda.app.server.role.UserRole
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class UserRoleConverter: AttributeConverter<UserRole, String> {
    override fun convertToDatabaseColumn(attribute: UserRole): String = attribute.value
    override fun convertToEntityAttribute(dbData: String): UserRole = UserRole(dbData)
}