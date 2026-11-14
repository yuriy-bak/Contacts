package org.fossify.contacts.activities

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import org.fossify.commons.extensions.toast
import org.fossify.commons.helpers.ContactsHelper
import org.fossify.commons.helpers.PERMISSION_READ_CONTACTS
import org.fossify.contacts.helpers.GROUP

/**
 * Handles ACTION_VIEW for content://com.android.contacts/groups/<id>
 * (MIME type vnd.android.cursor.item/group) and opens the group in [GroupContactsActivity].
 */
class ViewGroupActivity : SimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val groupId = intent.data?.let {
            try {
                ContentUris.parseId(it)
            } catch (e: Exception) {
                -1L
            }
        } ?: -1L

        if (groupId < 0) {
            toast(org.fossify.commons.R.string.unknown_error_occurred)
            finish()
            return
        }

        handlePermission(PERMISSION_READ_CONTACTS) { granted ->
            if (!granted) {
                toast(org.fossify.commons.R.string.no_contacts_permission)
                finish()
                return@handlePermission
            }

            ContactsHelper(this).getStoredGroups { groups ->
                val group = groups.firstOrNull { it.id == groupId }
                if (group == null) {
                    toast(org.fossify.commons.R.string.unknown_error_occurred)
                } else {
                    Intent(this, GroupContactsActivity::class.java).apply {
                        putExtra(GROUP, group)
                        startActivity(this)
                    }
                }
                finish()
            }
        }
    }
}
