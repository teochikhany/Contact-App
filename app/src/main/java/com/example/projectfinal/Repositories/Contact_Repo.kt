package com.example.projectfinal.Repositories

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.contact_app.Model.*
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.NameTuple


@Dao
interface Contact_Repo
{
    @Query("SELECT * FROM contact_info")
    suspend fun getAll(): List<Contact_Info>

    @Query("SELECT contact_id, first_name, last_name FROM contact_info order by first_name")
    suspend fun getFullName(): List<NameTuple>

    @Query("SELECT contact_id, first_name, last_name FROM contact_info where first_name LIKE '%' || :name || '%' or last_name LIKE '%' || :name || '%' order by first_name")
    suspend fun getFullName_filter(name: String): List<NameTuple>

    @Query("SELECT * FROM contact_info where contact_id = (:contactId)")
    suspend fun getFulContact(contactId: Int): Contact_Info

    @Query("SELECT contact_id, first_name, last_name FROM contact_info where contact_id = (:contactId)")
    suspend fun getContact(contactId: Int): NameTuple

    // @Query("SELECT contact_id, first_name, last_name FROM contact_info where first_name LIKE '%' || :first_name || '%' and last_name LIKE '%' || :last_name || '%' and middle_name LIKE '%' || :middle_name || '%' and job LIKE '%' || :job || '%' and phone_number LIKE '%' || :phone || '%' and email LIKE '%' || :email || '%' and organisation_id = :orgID and :customFields")
    // suspend fun getFulContactFilter(first_name: String, last_name: String, middle_name: String, job: String, phone: String, email: String, orgID: Long, customFields: String): List<NameTuple>

    @RawQuery
    suspend fun getFulContactFilter2(query: SupportSQLiteQuery) : List<NameTuple>

    @Insert
    suspend fun insert(user: Contact_Info): Long

    @Transaction
    @Query("SELECT * FROM contact_info")
    suspend fun getUsersAndLocation(): List<ContactWithLocation>


    // TODO ymkn hon ken fina de8re @Update w l parameter bi koun type: Contact_Info
    // hek bas 1 fct update badel l 8 li ta7et

    @Query("update contact_info set first_name = (:firstName) where contact_id = (:contactId)")
    suspend fun UpdateFirstName(contactId:Int, firstName: String)

    @Query("update contact_info set middle_name = (:middleName) where contact_id = (:contactId)")
    suspend fun UpdateMiddleName(contactId:Int, middleName: String)

    @Query("update contact_info set last_name = (:lastName) where contact_id = (:contactId)")
    suspend fun UpdateLastName(contactId:Int, lastName: String)

    @Query("update contact_info set organisation_id = (:organisation) where contact_id = (:contactId)")
    suspend fun UpdateOrgID(contactId:Int, organisation: Long)

    @Query("update contact_info set job = (:job) where contact_id = (:contactId)")
    suspend fun UpdateJOb(contactId:Int, job: String)

    @Query("update contact_info set phone_number = (:phone) where contact_id = (:contactId)")
    suspend fun UpdatePhone(contactId:Int, phone: String)

    @Query("update contact_info set email = (:email) where contact_id = (:contactId)")
    suspend fun UpdateEmail(contactId:Int, email: String)

    @Query("update contact_info set custom_fields = (:customField) where contact_id = (:contactId)")
    suspend fun UpdateCustom(contactId:Int, customField: String)

    @Query("delete from contact_info where contact_id = (:contactId)")
    suspend fun DeleteContact(contactId:Int)

}
