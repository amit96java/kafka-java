# Rewatch:

## sec01
### lec 44 

    (groud id and member id)

    if member id is not same than kafka wait for 45 sec and than share the message to 
    consumer with new member id

### lec 45

    now when we restart the application broker send all the message again and again to consumer
    this is a problem 

    broker track every event and if we do not acknowledge than after restarting the application 
    it will again send the all the message.

### lec 46

    if we don't want to acknowledge than we set enable.auto.commit = true

## sec07

### lec 62

    

