public class Admin extends ParkingManager
{ public Admin(String username, String password) 
  {  super(username, password);       }

  public ParkingManager register(String id, String pass)
  {  return new ParkingManager(id, pass);    }

  public void finalReport(ParkingRecord[] records, int recordCount)
  { double total = 0;
      System.out.println("\n--- Daily Report ---");
      for (int i = 0; i < recordCount; i++)
      {  System.out.println("Record ID: " + records[i].recordId + "   Vehicle: " + records[i].vehicle.vehicleNumber + "   Fee: " + records[i].fee);
         total += records[i].fee;
      }
      System.out.println("Total Revenue: Rs." + total);
  }
}     

