public class ParkingRecord 
{   int recordId;
    Vehicle vehicle;
    ParkingSlot slot;
    long entryTime;
    long exitTime;
    double fee;

    public ParkingRecord(int id, Vehicle v, ParkingSlot s) 
    {   this.recordId = id;
        this.vehicle = v;
        this.slot = s;
        this.entryTime = System.currentTimeMillis();
        this.fee = 0;
    }

    public long getDuration() 
    {   if (exitTime == 0) 
          return 0;
        else
          return (exitTime - entryTime) / 1000;         
    }

    public void closeRecord() 
    {   this.exitTime = System.currentTimeMillis();
        calculateFee();
    }

    public void calculateFee() 
    {   long durationSec = getDuration();
        this.fee = durationSec * (30.0/(2.0*60.0*60.0));       
    }
}
