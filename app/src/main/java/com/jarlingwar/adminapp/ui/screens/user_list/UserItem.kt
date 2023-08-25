package com.jarlingwar.adminapp.ui.screens.user_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.ui.common.MyIcon
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.utils.getTimeHyphen
import com.jarlingwar.adminapp.utils.round

@Composable
fun UserItem(user: UserModel, onClick: (UserModel) -> Unit) {
    Card(
        Modifier
            .height(120.dp)
            .clickable { onClick(user) },
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.adminColors.backgroundSecondary
    ) {
        Column(Modifier.padding(5.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center) {
                    val profileBgColor = if (user.verified) MaterialTheme.adminColors.primary
                    else MaterialTheme.adminColors.dangerPrimary
                    Box(
                        modifier = Modifier
                            .background(profileBgColor, CircleShape)
                            .size(53.dp)
                    )
                    MyImage(
                        modifier = Modifier.size(50.dp),
                        imgUrl = user.profileImageUrl,
                        shape = CircleShape
                    )
                }

                Column(
                    Modifier
                        .padding(start = 5.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = user.email,
                        style = Type.Body2,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = user.displayName,
                        style = Type.Body3,
                        color = MaterialTheme.adminColors.textSecondary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Row {
                        MyIcon(R.drawable.ic_star)
                        val ratingsText = if (user.ratings.isNotEmpty()) "${user.ratings.size} - ${
                            user.ratings.average().round()
                        }" else "0"
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = ratingsText,
                            style = Type.Body2
                        )
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(id = R.string.reports),
                    style = Type.Body1,
                    color = MaterialTheme.adminColors.textSecondary
                )
                Text(text = user.reports.toString(), style = Type.Body1)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(id = R.string.created),
                    style = Type.Body1,
                    color = MaterialTheme.adminColors.textSecondary
                )
                Text(text = getTimeHyphen(user.created), style = Type.Body1)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(id = R.string.listings),
                    style = Type.Body1,
                    color = MaterialTheme.adminColors.textSecondary
                )
                Text(text = user.listingsId.size.toString(), style = Type.Body1)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserItemPreview() {
    AdminAppTheme {
        UserItem(user = UserModel.getMock()) {

        }
    }
}