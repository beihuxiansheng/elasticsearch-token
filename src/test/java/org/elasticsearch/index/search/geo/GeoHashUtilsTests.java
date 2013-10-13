begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoHashUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoPoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticSearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoHashUtilsTests
specifier|public
class|class
name|GeoHashUtilsTests
extends|extends
name|ElasticSearchTestCase
block|{
comment|/**      * Pass condition: lat=42.6, lng=-5.6 should be encoded as "ezs42e44yx96",      * lat=57.64911 lng=10.40744 should be encoded as "u4pruydqqvj8"      */
annotation|@
name|Test
DECL|method|testEncode
specifier|public
name|void
name|testEncode
parameter_list|()
block|{
name|String
name|hash
init|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|42.6
argument_list|,
operator|-
literal|5.6
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ezs42e44yx96"
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|hash
operator|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|57.64911
argument_list|,
literal|10.40744
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"u4pruydqqvj8"
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
comment|/**      * Pass condition: lat=52.3738007, lng=4.8909347 should be encoded and then      * decoded within 0.00001 of the original value      */
annotation|@
name|Test
DECL|method|testDecodePreciseLongitudeLatitude
specifier|public
name|void
name|testDecodePreciseLongitudeLatitude
parameter_list|()
block|{
name|String
name|hash
init|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|52.3738007
argument_list|,
literal|4.8909347
argument_list|)
decl_stmt|;
name|GeoPoint
name|point
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|hash
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|52.3738007
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|,
literal|0.00001D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.8909347
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
literal|0.00001D
argument_list|)
expr_stmt|;
block|}
comment|/**      * Pass condition: lat=84.6, lng=10.5 should be encoded and then decoded      * within 0.00001 of the original value      */
annotation|@
name|Test
DECL|method|testDecodeImpreciseLongitudeLatitude
specifier|public
name|void
name|testDecodeImpreciseLongitudeLatitude
parameter_list|()
block|{
name|String
name|hash
init|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|84.6
argument_list|,
literal|10.5
argument_list|)
decl_stmt|;
name|GeoPoint
name|point
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|hash
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|84.6
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|,
literal|0.00001D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.5
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
literal|0.00001D
argument_list|)
expr_stmt|;
block|}
comment|/*     * see https://issues.apache.org/jira/browse/LUCENE-1815 for details     */
annotation|@
name|Test
DECL|method|testDecodeEncode
specifier|public
name|void
name|testDecodeEncode
parameter_list|()
block|{
name|String
name|geoHash
init|=
literal|"u173zq37x014"
decl_stmt|;
name|assertEquals
argument_list|(
name|geoHash
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|52.3738007
argument_list|,
literal|4.8909347
argument_list|)
argument_list|)
expr_stmt|;
name|GeoPoint
name|decode
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|52.37380061d
argument_list|,
name|decode
operator|.
name|lat
argument_list|()
argument_list|,
literal|0.000001d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.8909343d
argument_list|,
name|decode
operator|.
name|lon
argument_list|()
argument_list|,
literal|0.000001d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|geoHash
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
name|decode
operator|.
name|lat
argument_list|()
argument_list|,
name|decode
operator|.
name|lon
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

