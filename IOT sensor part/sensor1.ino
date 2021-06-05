#include <SparkFunRHT03.h>

#include "application.h"
#include "HttpClient.h"
#include <Grove_LCD_RGB_Backlight.h>


rgb_lcd lcd;
 
const int colorR = 255;
const int colorG = 0;
const int colorB = 0;


const int RHT03_DATA_PIN = 4; // RHT03 data pin

RHT03 rht; 

const int pinAdc = A0;

unsigned int nextTime = 0;    // Next time to contactthe server
HttpClient http;
// Headers currently need to be set at init, usefulfor API keys etc.
http_header_t headers[] = 
{
    //  { "Content-Type", "application/json" },
    //  { "Accept" , "application/json" },
    { "Accept" , "*/*"},
    { NULL, NULL } // NOTE: Always terminate headerswill NULL
};
http_request_t request;
http_response_t response;

void setup() {
    
    Serial.begin(9600);
    rht.begin(RHT03_DATA_PIN);
    
    lcd.begin(16, 2);
 
    lcd.setRGB(colorR, colorG, colorB);
}


void loop() {
    /*
    int updateRet = rht.update();
	
	// If successful, the update() function will return 1.
	// If update fails, it will return a value <0
	float latestHumidity = rht.humidity();
	float latestTempC = rht.tempC();
	float latestTempF = rht.tempF();
	*/
	
	long sum = 0;
    for(int i=0; i<32; i++)
    {
        sum += analogRead(pinAdc);
    }
 
    sum >>= 5;
 
    Serial.println(sum);
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Volume: ");
    lcd.print(sum);
    
    if (sum > 4000){
        lcd.setCursor(0, 1);
        lcd.print("TOO LOUD!");
    }
// 	Serial.println("Humidity: " + String(latestHumidity, 1) + " %");
// 	Serial.println("Temp (F): " + String(latestTempF, 1) + " deg F");
    

    Serial.println("Application>\tStart of Loop.");// Request path and body can be set at runtimeor at setup.
    request.hostname = "18.117.75.236";
    request.port = 5000;
    request.path = "/api/upload1/?humidity=" + String(sum) + "&temp=" + String(0);
    // The library also supports sending a body withyour request:
    //request.body = "{\"key\":\"value\"}";
    // Get request
    http.get(request, response, headers);
    Serial.print("Application>\tResponse status: ");
    Serial.println(response.status);
    Serial.print("Application>\tHTTP Response Body:");
    Serial.println(response.body);
    nextTime = millis()+10;
    
    delay(10);

}
    