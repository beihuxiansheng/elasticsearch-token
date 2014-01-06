begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  * Represents the state that a snapshot can be in  */
end_comment

begin_enum
DECL|enum|SnapshotState
specifier|public
enum|enum
name|SnapshotState
block|{
comment|/**      * Snapshot process has started      */
DECL|enum constant|IN_PROGRESS
name|IN_PROGRESS
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
comment|/**      * Snapshot process completed successfully      */
DECL|enum constant|SUCCESS
name|SUCCESS
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
comment|/**      * Snapshot failed      */
DECL|enum constant|FAILED
name|FAILED
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|SnapshotState
specifier|private
name|SnapshotState
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Returns code that represents the snapshot state      *      * @return code for the state      */
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Returns true if snapshot completed (successfully or not)      *      * @return true if snapshot completed, false otherwise      */
DECL|method|completed
specifier|public
name|boolean
name|completed
parameter_list|()
block|{
return|return
name|this
operator|==
name|SUCCESS
operator|||
name|this
operator|==
name|FAILED
return|;
block|}
comment|/**      * Generate snapshot state from code      *      * @param value the state code      * @return state      */
DECL|method|fromValue
specifier|public
specifier|static
name|SnapshotState
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|0
case|:
return|return
name|IN_PROGRESS
return|;
case|case
literal|1
case|:
return|return
name|SUCCESS
return|;
case|case
literal|2
case|:
return|return
name|FAILED
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No snapshot state for value ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

