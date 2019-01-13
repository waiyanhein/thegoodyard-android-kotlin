package com.thegoodyard.waiyanhein.thegoodyard


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@DynamoDBTable(tableName = "thegoodyardandroid-mobilehub-330286608-item")
class ItemDO {
    @DynamoDBHashKey(attributeName = "Id")
    @DynamoDBAttribute(attributeName = "Id")
    var id: String? = null

    @DynamoDBAttribute(attributeName = "Description")
    var description: String? = null

    @DynamoDBAttribute(attributeName = "Name")
    var name: String? = null

    @DynamoDBAttribute(attributeName = "UserId")
    var userId: String? = null

    @DynamoDBAttribute(attributeName = "Images")
    var images: List<String>? = null

    @DynamoDBAttribute(attributeName = "Latitude")
    var latitude : Double? = null

    @DynamoDBAttribute(attributeName = "Longitude")
    var longitude : Double? = null

    @DynamoDBAttribute(attributeName = "Thumbnail")
    var thumbnail: String? = null
}
