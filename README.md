## Inspiration

This is a challenge proposed by Vodafone. 
According to them, 80% of the customers look for a new phone online, and already know what they want by the time they hit the stores. The only reason why the customer does not buy the phone directly from the site is that they want to get a feel for the phone, and how that compares to their current phone.

## What it does

This application helps users to view and compare phones in AR. The user starts the application and the camera will be activated. The user then can click on a point in the camera and they will be asked what phone they want to place at that point. The desired phone will then appear there, with the actual size and looks. The user can also choose to place multiple phones  and compare their specifications. 

## How we built it

We've used Google's ARcore library combined with Kotlin to develop this android application that communicates with a PHP server for phone images and specifications.  The app will request a list of phones that match the user input and put them in autocomplete form for the user to select, the server then will call Fono API to get all the specifications for the phone and scrape Emag for pictures for the phone. When the app has those, it will generate a 3D model from the received images and display the phone alongside it's specifications. 

## Challenges we ran into

Generating models, 3D math computations, making sure that the phones are generated with their correct size, placing input directly into AR.

## Accomplishments that we're proud of

We learned and managed to do this application in a short amount of time. We managed to generate phones programmatically and actually managed to make them look ok. 

## What we learned

We've learned to use ARcore, as this was the first time using it. We've learned Kotlin and we've learned about the challenges of 3D.

## What's next for PhoneAR

We wish to test it with real people that want to buy a phone and get their input on the application. Until then, upgrading the phone models, making comparing phones easier by using statistics like the point system used by AnTuTu Benchmark and color coding specifications ( green for the better one, red for the worse )  
