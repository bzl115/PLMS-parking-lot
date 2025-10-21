public class ParkingManager
{  protected String managerId;
   protected String password;

   public ParkingManager(String id, String pswd)
   {  managerId = id;
      password = pswd;
   }

   public boolean login(String user, String pass)
   {  return managerId.equals(user) && password.equals(pass);     }
    
   public void logout()
   {  System.out.println( managerId + " logged out");    }

   public void assignSlotToVehicle(ParkingSlot slot, Vehicle v)
   {  if (slot.checkAvailability())
      {  slot.assignSlot();
         System.out.println("Slot " + slot.slotId + " assigned to Vehicle " + v.vehicleNumber);
      }
      else
         System.out.println("Slot " + slot.slotId + " is not available!");
   }

   public void releaseSlotForVehicle(ParkingSlot slot)
   {  slot.freeSlot();
      System.out.println("Slot " + slot.slotId + " is now free.");
   }

   
   public void viewAvailableSlots(ParkingSlot[] slots, ParkingRecord[] records, int recordCount)
   {  for (int i = 0; i < slots.length; i++) 
      {  Vehicle occupyingVehicle = null;
         if (!slots[i].checkAvailability()) 
         {  for (int j = 0; j < recordCount; j++) 
            {  if (records[j].slot.slotId == slots[i].slotId && records[j].exitTime == 0) 
               {  occupyingVehicle = records[j].vehicle;
                  break;
               }
            }
         }
         slots[i].printStatus(occupyingVehicle);
      }
   }
}