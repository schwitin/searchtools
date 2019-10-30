const int STEP_PIN=9;
        const int Enable_PIN=13;
        const int DIR_PIN=8;
        long currentMicros=0;
        long previousMicros=0;
//float accel =100;
//float d =accel*0.4;
//float h= d/100;


        void setup()
        {
        Serial.begin(9600);
        pinMode(STEP_PIN,OUTPUT);
        pinMode(Enable_PIN,OUTPUT);
        pinMode(DIR_PIN,OUTPUT);
        digitalWrite(STEP_PIN,LOW);
        digitalWrite(Enable_PIN,HIGH);
        digitalWrite(DIR_PIN,HIGH);
        Serial.println("Welcome to Rhino Motion Control");
        Serial.println("Press a for 200 steps/rev ");
        Serial.println("Press b for 400 steps/rev ");
        Serial.println("Press c for 800 steps/rev ");
        Serial.println("Press d for 3200 steps/rev ");
        Serial.println("Press e for 6400 steps/rev ");

        }

        void loop()
        {

        if(Serial.available())

        {

        int drukdata=Serial.read();

        if(drukdata=='a')
        {
        Move1();
        }

        if(drukdata=='b')       // Compare serial data
        {
        Move2();
        }

        if(drukdata=='c')       // Compare serial data
        {
        Move3();
        }

        if(drukdata=='d')       // Compare serial data
        {
        Move4();
        }

        if(drukdata=='e')       // Compare serial data
        {
        Move5();
        }

        }

        }


        void Move1()                 //Forward Function
        {
        Serial.println("Give the Value for Accel_Deaccel in %(30-100%)");
        while(Serial.available()==0);

        int val=Serial.parseInt();
        Serial.println(val);//read int or parseFloat for ..float...
        float d=val*0.4;
        float h=d/100;
        Serial.println("Give the Value for Seteps ");
        while(Serial.available()==0);

        int S=Serial.parseInt();
        Serial.println(S);

        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float b=333;
        float a=2000;
        for(int i=0;i<S; i++)
        {
//        a=a-2;
        if(a<=b)a=b;
        else a=a-h;
        long delay_Micros=a;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(a);
        digitalWrite(STEP_PIN,LOW);
        }

        }


        if(long delay_Micros=500)
        {
        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float c=333;
        float d=2000;
        for(int j=0;j<S/5;j++)
        {
//        a=a-2;
        if(d<=c)d=c;
        else c=c+h;
        long delay_Micros=c;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(c);
        digitalWrite(STEP_PIN,LOW);
        }
        }

        delay(2000);

        }}

        void Move2()                 //Forward Function
        {

        Serial.println("Give the Value for Accel_Deaccel in %(30-100%)");
        while(Serial.available()==0);

        int val=Serial.parseInt();
        Serial.println(val);//read int or parseFloat for ..float...
        float d=val*0.4;
        float h=d/100;
        Serial.println("Give the Value for Seteps ");
        while(Serial.available()==0);

        int S=Serial.parseInt();
        Serial.println(S);

        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float b=127;
        float a=2000;
        for(int i=0;i<S; i++)
        {
//        a=a-2;
        if(a<=b)a=b;
        else a=a-h;
        long delay_Micros=a;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(a);
        digitalWrite(STEP_PIN,LOW);
        }

        }


        if(long delay_Micros=500)
        {
        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float c=127;
        float d=2000;
        for(int j=0;j<S/5;j++)
        {
//        a=a-2;
        if(d<=c)d=c;
        else c=c+h;
        long delay_Micros=c;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(c);
        digitalWrite(STEP_PIN,LOW);
        }
        }

        delay(2000);

        }}


        void Move3()                 //Forward Function
        {

        Serial.println("Give the Value for Accel_Deaccel in %(30-100%)");
        while(Serial.available()==0);

        int val=Serial.parseInt();
        Serial.println(val);//read int or parseFloat for ..float...
        float d=val*0.4;
        float h=d/100;
        Serial.println("Give the Value for Seteps ");
        while(Serial.available()==0);

        int S=Serial.parseInt();
        Serial.println(S);

        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float b=80;
        float a=2000;
        for(int i=0;i<S; i++)
        {
//        a=a-2;
        if(a<=b)a=b;
        else a=a-h;
        long delay_Micros=a;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(a);
        digitalWrite(STEP_PIN,LOW);
        }

        }


        if(long delay_Micros=500)
        {
        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float c=80;
        float d=2000;
        for(int j=0;j<S/5;j++)
        {
//        a=a-2;
        if(d<=c)d=c;
        else c=c+h;
        long delay_Micros=c;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(c);
        digitalWrite(STEP_PIN,LOW);
        }
        }

        delay(2000);

        }}

        void Move4()                 //Forward Function
        {

        Serial.println("Give the Value for Accel_Deaccel in %(30-100%)");
        while(Serial.available()==0);

        int val=Serial.parseInt();
        Serial.println(val);//read int or parseFloat for ..float...
        float d=val*0.4;
        float h=d/100;
        Serial.println("Give the Value for Seteps ");
        while(Serial.available()==0);

        int S=Serial.parseInt();
        Serial.println(S);

        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float b=25;
        float a=2000;
        for(int i=0;i<S; i++)
        {
//        a=a-2;
        if(a<=b)a=b;
        else a=a-h;
        long delay_Micros=a;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(a);
        digitalWrite(STEP_PIN,LOW);
        }

        }


        if(long delay_Micros=500)
        {
        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float c=25;
        float d=2000;
        for(int j=0;j<S/5;j++)
        {
//        a=a-2;
        if(d<=c)d=c;
        else c=c+h;
        long delay_Micros=c;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(c);
        digitalWrite(STEP_PIN,LOW);
        }
        }

        delay(2000);

        }}

        void Move5()                 //Forward Function
        {

        Serial.println("Give the Value for Accel_Deaccel in %(30-100%)");
        while(Serial.available()==0);

        int val=Serial.parseInt();
        Serial.println(val);//read int or parseFloat for ..float...
        float d=val*0.4;
        float h=d/100;
        Serial.println("Give the Value for Seteps ");
        while(Serial.available()==0);

        int S=Serial.parseInt();
        Serial.println(S);

        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float b=5;
        float a=2000;
        for(int i=0;i<S; i++)
        {
//        a=a-2;
        if(a<=b)a=b;
        else a=a-h;
        long delay_Micros=a;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(a);
        digitalWrite(STEP_PIN,LOW);
        }

        }


        if(long delay_Micros=500)
        {
        digitalWrite(Enable_PIN,LOW);
        digitalWrite(DIR_PIN,HIGH);
        float c=5;
        float d=2000;
        for(int j=0;j<S/5;j++)
        {
//        a=a-2;
        if(d<=c)d=c;
        else c=c+h;
        long delay_Micros=c;
        currentMicros=micros();
        if(currentMicros-previousMicros>=delay_Micros)
        {
        previousMicros=currentMicros;

        digitalWrite(STEP_PIN,HIGH);
        delayMicroseconds(c);
        digitalWrite(STEP_PIN,LOW);
        }
        }

        delay(2000);

        }}

