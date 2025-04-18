package com.example.whatsappp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappp.R

@Preview(showBackground = true)
@Composable
fun siginscreenui(onSigninClick: () -> Unit = {}){

    val brush = Brush.linearGradient(
        listOf(
            Color(0xFF238CDD),
            Color(0xFF255DCC)
        )
    )
    Image(
        painter = painterResource(id = R.drawable.login_blur),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(80.dp))
        Image(
            painter = painterResource(id = R.drawable.oig4__rndcloiljdx4hxpn),
            contentDescription = null
        )

        Text(
            text = "Lets Chat!",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
            fontFamily = FontFamily.Cursive,
            color = Color(0xFF255DCC)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally, // Center the button horizontally
            verticalArrangement = Arrangement.Center // Center the button vertically (optional)
        ) {
            Button(
                onClick = { onSigninClick() },
                modifier = Modifier
                    .background(brush, CircleShape)
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = CircleShape
            ) {
                Text(
                    text = "Continue With Google",
                    color = Color.White,
                    modifier = Modifier.padding(end = 20.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Image(
                    painter = painterResource(id = R.drawable.goog_0ed88f7c),
                    contentDescription = null,
                    modifier = Modifier.scale(1.2f)
                )
            }
        }

    }
}