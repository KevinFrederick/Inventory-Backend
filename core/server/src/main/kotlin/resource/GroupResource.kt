package resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/groups")
class GroupResource {

    @Serializable
    @Resource("{groupId}")
    class Id (val parent: GroupResource, val groupId: String) {

        @Serializable
        @Resource("members")
        class Members(val parent: Id) {

            @Serializable
            @Resource("{memberId}")
            class Member (val parent: Members, val memberId: String)

            @Serializable
            @Resource("pending")
            class Pending(val parent: Members)

            @Serializable
            @Resource("{targetUserId}/approve")
            class Approve(val parent: Members, val targetUserId: String)

            @Serializable
            @Resource("{targetUserId}/reject")
            class Reject(val parent: Members, val targetUserId: String)

        }

        @Serializable
        @Resource("invites")
        class Invites(val parent: Id)
    }
}