
const API_GETORDERS = new URL("./api/admin/getorders", document.location);

const API_GETSTOCK = new URL("./api/admin/getstock", document.location);
const API_SETSTOCK = new URL("./api/admin/setstock", document.location);
const API_RESETDB = new URL("./api/reset", document.location);

SKU_SIZE = {"S": "S",
            "M": "M",
            "L": "L",
            "X": "XL",
            "2": "XXL"}

SKU_COLOUR = {"BK": "Black",
              "GY": "Grey",
              "WE": "White",
              "RD": "Red",
              "GN": "Green",
              "BE": "Blue",
              "PE": "Purple"}

SKU_QUALITY = {"S": "Standard",
               "P": "Supreme"}

function resetDatabase() {
  if (confirm("Are you sure you want to clear and reset the database?")){
    let http = new XMLHttpRequest();
		
		http.onreadystatechange = function(){ 
			if (http.readyState == 4) {
				if (http.status == 200) {
					location.reload(); 
				}
			}
		}
		http.open("GET", API_RESETDB, true);
		http.send(null);
  }
}

function setStock(sku, qty){
  let f = new FormData()
  f.append("sku", sku);
  f.append("amount", qty);

  let http = new XMLHttpRequest();
  
  http.onreadystatechange = function(){ 
    if (http.readyState == 4) {
      let resp = JSON.parse(http.responseText);

      if (http.status == 200) {
        getStock();
      }
    }
  }
  
  http.open("POST", API_SETSTOCK, true);
  http.send(f);
}

function getStock() {
  
  
  let http = new XMLHttpRequest();
  
  http.onreadystatechange = function(){ 
    if (http.readyState == 4) {
      if (http.status == 200) {

        // lord forgive me for the sins ahead

        stock.innerHTML = "<tr><th>SKU</th><th>Size</th><th>Colour</th><th>Quality</th><th>Quantity</th><th>Apply</th></tr>";
        let resp = JSON.parse(http.responseText);
        for (i=0; i<resp.length; i++){
          let sku = resp[i][0];
          let quantity = resp[i][1];
          let row = stock.insertRow(i+1);
          
          row.insertCell(0).innerText = sku;
          row.insertCell(1).innerText = SKU_SIZE[sku[0]];
          row.insertCell(2).innerText = SKU_COLOUR[sku.slice(1,3)];
          row.insertCell(3).innerText = SKU_QUALITY[sku[3]];
          row.insertCell(4).innerHTML = `<input id='qtyEntry${sku}' type='text' value='${quantity}'>`;
          row.insertCell(5).innerHTML = `<button onclick="setStock('${sku}', qtyEntry${sku}.value)" value='Set'>Set</button>`;
        }
        
      }
    }
  }
  http.open("GET", API_GETSTOCK, true);
  http.send(null);
  
}


function getOrders(){
  let http = new XMLHttpRequest();
  
  http.onreadystatechange = function(){ 
    if (http.readyState == 4) {
      if (http.status == 200) {
        orders.innerHTML = "<tr><th>Order ID</th><th>Date</th><th>Full Name</th><th>Address</th><th>SKU</th><th>Quantity</th></tr>";
        let resp = JSON.parse(http.responseText);
        for (i=0; i<resp.length; i++){
          let id = resp[i][0];
          let date = resp[i][1];
          let fullname =  resp[i][2];
          let address =  resp[i][3];
          let sku =  resp[i][4];
          let quantity =  resp[i][5];
          let row = orders.insertRow(i+1);
          // forgive me lord for i have sinned ahead
          row.insertCell(0).innerText = id;
          row.insertCell(1).innerText = date;
          row.insertCell(2).innerText = fullname;
          row.insertCell(3).innerText = address;
          row.insertCell(4).innerText = sku;
          row.insertCell(5).innerText = quantity;

        }
        
      }
    }
  }
  http.open("GET", API_GETORDERS, true);
  http.send(null);
  
}


window.onload = () => {

  
  getStock();
  
  
	stockPageButton.onclick = () => {
    getStock();
		orderPage.className = "hidden";
    stockPage.className = "";
    stockPageButton.children[0].className = "active";
    orderPageButton.children[0].className = "";
    
    
    
		return false;
	}
  
	orderPageButton.onclick = () => {
    getOrders();
		orderPage.className = "";
    stockPage.className = "hidden";
    stockPageButton.children[0].className = "";
    orderPageButton.children[0].className = "active";
    
    
    
		return false;
	}
  
  
	
}
