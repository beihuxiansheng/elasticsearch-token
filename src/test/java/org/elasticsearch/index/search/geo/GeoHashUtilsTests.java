begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticsearchTestCase
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ElasticsearchTestCase
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
annotation|@
name|Test
DECL|method|testNeighbours
specifier|public
name|void
name|testNeighbours
parameter_list|()
block|{
name|String
name|geohash
init|=
literal|"gcpv"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedNeighbors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"gcpw"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"gcpy"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u10n"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"gcpt"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u10j"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"gcps"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"gcpu"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u10h"
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|?
super|super
name|String
argument_list|>
name|neighbors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedNeighbors
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
comment|// Border odd geohash
name|geohash
operator|=
literal|"u09x"
expr_stmt|;
name|expectedNeighbors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u0c2"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u0c8"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u0cb"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09r"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09z"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09q"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09w"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09y"
argument_list|)
expr_stmt|;
name|neighbors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedNeighbors
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
comment|// Border even geohash
name|geohash
operator|=
literal|"u09tv"
expr_stmt|;
name|expectedNeighbors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09wh"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09wj"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09wn"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09tu"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09ty"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09ts"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09tt"
argument_list|)
expr_stmt|;
name|expectedNeighbors
operator|.
name|add
argument_list|(
literal|"u09tw"
argument_list|)
expr_stmt|;
name|neighbors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedNeighbors
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

