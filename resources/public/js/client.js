const API_INSTOCK = new URL("./api/client/instock", document.location);
const API_BUY = new URL("./api/client/buy", document.location);



window.onload = () => {
  function getSKU() {
    return productForm["size"].value
         + productForm["colour"].value 
         + productForm["quality"].value;
  }
  
	let colourRadios = document.querySelectorAll('.radio-colour');

	colourRadios.forEach(item => {
		item.addEventListener('click', event => {
			colourRadios.forEach(item => {item.classList.remove("active");});
			item.classList.add("active");
			productForm["colour"].value = item.getAttribute("data-colour");
		})
	})
	
	
	productForm.onsubmit = () => {
		productStatus.className = "hidden";
		payButton.innerText = "Checking stock...";
		payButton.disabled = true;
		
		let sku = getSKU();
				
		let q = new URLSearchParams();
		
		q.set("sku", sku);
		q.set("amount", productForm["qty"].value);
		
		let http = new XMLHttpRequest();
		
		http.onreadystatechange = function(){ 
			if (http.readyState == 4) {
				if (http.status == 200) {
					let resp = JSON.parse(http.responseText);
          payButton.innerText = "Enter Shipping Details";
          payButton.disabled = false;
          if (resp["available"]) {
            product.className = "hidden";
            shipping.className = "";
          } else {
            productStatus.innerText = "Item not available in desired quantity"
            productStatus.className = "status-message";
          }
          
				} else {
					productStatus.innerText = "An error has occured, please try again"
					productStatus.className = "status-message";
				}
			}
		}
		
		API_INSTOCK.search = q.toString();
		
		http.open("GET", API_INSTOCK, true);
		http.send(null);
		
		return false;
	}
  
  
  
  
  shippingBackButton.onclick = () => {
    shippingStatus.className = "hidden";
    product.className = "";
    shipping.className = "hidden";
    return false;
  }
  
  
  
  
  shippingForm.onsubmit = () => {
    let f = new FormData(shippingForm);
    f.append("sku", getSKU());
    f.append("amount", productForm["qty"].value);
    
    
    let http = new XMLHttpRequest();
		
		http.onreadystatechange = function(){ 
			if (http.readyState == 4) {
        let resp = JSON.parse(http.responseText);
        
				if (http.status == 200) {
          if ("error" in resp) {
            shippingStatus.innerText = resp["error"];
            shippingStatus.className = "status-message";
          } else {
            if (resp["success"]) {
              shipping.className = "hidden";
              confirmed.className = "";
            } else {
              shippingStatus.innerText = "Item not available in desired quantity"
              shippingStatus.className = "status-message";
            }
          }
          
				} else {
					shippingStatus.innerText = "An error has occured, please try again"
					shippingStatus.className = "status-message";
				}
			}
		}
		
		
		http.open("POST", API_BUY, true);
		http.send(f);

		return false;
  }
  
  
  
  
	
}