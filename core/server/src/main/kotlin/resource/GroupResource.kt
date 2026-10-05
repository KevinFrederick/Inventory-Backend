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
        }
    }
}