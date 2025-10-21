public class ParkingSlot
{  int slotId;
   boolean isAvailable;

   public ParkingSlot(int id) 
   {  slotId = id;
      isAvailable = true;
   }
   
   public void assignSlot()
   {  isAvailable=false;     }
   
   public void freeSlot()
   {  isAvailable=true;      }
   
   public boolean checkAvailability()
   {  return isAvailable;    }

   public void printStatus(Vehicle v) 
   { if (isAvailable) 
        System.out.println("Slot " + slotId + " is available");
    else
        System.out.println("Slot " + slotId + " is occupied by vehicle " + v.vehicleNumber );
  }
  
  // *** NEW METHOD FOR GUI COMPATIBILITY ***
  // It takes records/recordCount to find the current vehicle, replacing logic from Main.java
  public String getStatus(ParkingRecord[] records, int recordCount) {
     if (isAvailable) {
         return "Slot " + slotId + " is available";
     } else {
         Vehicle occupyingVehicle = null;
         for (int k = 0; k < recordCount; k++) {
             // Check for an active record (exitTime == 0) for this slot
             if (records[k].slot.slotId == slotId && records[k].exitTime == 0) {
                 occupyingVehicle = records[k].vehicle;
                 break;
             }
         }
         // Fallback just in case, but should always find the vehicle if !isAvailable
         String vehicleNum = (occupyingVehicle != null) ? occupyingVehicle.vehicleNumber : "UNKNOWN";
         return "Slot " + slotId + " is occupied by vehicle " + vehicleNum;
     }
  }
}