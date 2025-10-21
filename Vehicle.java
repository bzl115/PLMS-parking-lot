public class Vehicle{  
   int vehicleId;
   String vehicleNumber, ownerName,vehicleType;

   Vehicle(int id,String num,String owner, String type){
      vehicleId=id;
      vehicleNumber=num;
      ownerName=owner;
      vehicleType=type;
   }

   public void getVehicleInfo(){  
       System.out.println("ID: " + vehicleId +"   Number: " + vehicleNumber +"   Owner: " + ownerName +"   Type: " + vehicleType);   
   }
   
   // *** NEW METHOD FOR GUI COMPATIBILITY ***
   public String getVehicleInfoString(){ 
      return "ID: " + vehicleId +"   Number: " + vehicleNumber +"   Owner: " + ownerName +"   Type: " + vehicleType; 
   }
}