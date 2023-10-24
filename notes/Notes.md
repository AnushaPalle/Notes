@ResponseBody : serializes an object into JSON, can be applied or annotated on top of a method or class  
@RestController : = @Controller + @ResponseBody, => can be applied on top of class  
@ResponseEntity : allows us to return data and a status code: instead of  
``` @GetMapping("/contact/{id}")  
 public Contact getContact(@PathVariable String id){
     return new Contact("123","John","9876543210");  
}
```
to send status code along with data: below snippet can be replaced
```@GetMapping("/contact/{id}")  
 public ResponseEntity<Contact> getContact(@PathVariable String id){  
     Contact contact = contactService.getContactById(id);  
     return new ResponseEntity<>(contact, HttpStatus.OK);  
}
```  
@RequestBody : deserialises the JSON ( contents of the request body ) into Java object, it can be applied at the front of the variables while accepting method params in the method signature, for eg:  
```@PostMapping("/contact")  
public ResponseEntity<HttpStatus> createContact(@RequestBody Contact contact){  
     contactService.saveContact(contact);  
     return new ResponseEntity<>(HttpStatus.CREATED);  
}  
```
