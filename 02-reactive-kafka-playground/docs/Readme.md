# Rewatch:

## sec01
### lec 44 

    (group id and member id)

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

### lec 70 and 71

    producer sends data for kafka server and kafka server returns acknowledgement about we have
    received the data, and there are types of acknowledgement.
    

## sec10

### lec 82

    flat map for parallel processing
    in flat map we have issue of message unordering

### lec 81

    concatMap --------------------------- Sequential Batch Processing
    flatMap   --------------------------- parallel Batch Processing
    groupBy + flatMap ------------------- Parallel Batch Processing with message ordering

