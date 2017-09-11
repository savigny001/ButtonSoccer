package com.gushikustudios.rube.loader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.soccer.FieldData;

/**
 * Simple class for spatial (image) rendering.  If a body reference is included, its position will serve as the local coordinate system.
 * 
 * @author tescott
 *
 */
public class SimpleSpatial {
	public Sprite mSprite;
	private Body mBody;
	private final Vector2 mCenter = new Vector2();
	private final Vector2 mHalfSize = new Vector2();
	private float mRotation;
	private static final Vector2 mTmp = new Vector2();

	public SimpleSpatial(Texture texture, boolean flip, Body body, Color color, Vector2 size,
			Vector2 center, float rotationInDegrees) {
		mSprite = new Sprite(texture);
		defineSpatial(flip,body,color,size,center,rotationInDegrees);
	}

	public SimpleSpatial(TextureRegion region, boolean flip, Body body, Color color,
			Vector2 size, Vector2 center, float rotationInDegrees) {
		mSprite = new Sprite(region);
		defineSpatial(flip,body,color,size,center,rotationInDegrees);
	}
	
	public void defineSpatial(boolean flip, Body body, Color color, Vector2 size,
			Vector2 center, float rotationInDegrees)
	{
//		size.x *= 100;
//		size.y *= 100;
//
//		center.x *= 100;
//		center.y *= 100;



		mBody = body;
		mSprite.flip(flip, false);
		mSprite.setColor(color);
		mRotation = rotationInDegrees;
		mSprite.setSize(size.x, size.y);
		mSprite.setOrigin(size.x / 2, size.y / 2);
		mHalfSize.set(size.x / 2, size.y / 2);
		mCenter.set(center);

//		mCenter.x *=32;
//		mCenter.y *=32;


		if (body != null) {
			mTmp.set(body.getPosition());
//			mTmp.x *= 100;
//			mTmp.y *= 100;
			mSprite.setPosition(mTmp.x - size.x / 2 ,
					mTmp.y - size.y / 2 );

			float angle = mBody.getAngle() * MathUtils.radiansToDegrees;

			Vector2 mBodyPos = mBody.getPosition();
//			mBodyPos.x *= 100;
//			mBodyPos.y *= 100;


//			mTmp.set(mCenter).rotate(angle).add(mBody.getPosition())
//					.sub(mHalfSize);
			mTmp.set(mCenter).rotate(angle).add(mBodyPos
					.sub(mHalfSize));

			mSprite.setRotation(mRotation + angle);
		} else {
			mTmp.set(center.x - size.x / 2, center.y - size.y / 2);
			mSprite.setRotation(rotationInDegrees);
		}

		mSprite.setPosition(mTmp.x, mTmp.y);
	}

	public void render(SpriteBatch batch, float delta) {
		// if this is a dynamic spatial...
		if (mBody != null) {
			// use body information to render it...
			float angle = mBody.getAngle() * MathUtils.radiansToDegrees;

			mTmp.x *= FieldData.PIXELS_TO_METERS;
			mTmp.y *= FieldData.PIXELS_TO_METERS;

			Vector2 mBodyPos = mBody.getPosition();
			mBodyPos.x *= FieldData.PIXELS_TO_METERS;
			mBodyPos.y *= FieldData.PIXELS_TO_METERS;


			Vector2 newCenter = new Vector2(mCenter.x, mCenter.x);
			newCenter.x *= FieldData.PIXELS_TO_METERS;
			newCenter.y *= FieldData.PIXELS_TO_METERS;
			newCenter.y *= (FieldData.SCREEN_HEIGHT/FieldData.SCREEN_WIDTH);

			Vector2 newHalfSize = new Vector2(mHalfSize.x, mHalfSize.y);
			newHalfSize.x *= FieldData.PIXELS_TO_METERS;
			newHalfSize.y *= FieldData.PIXELS_TO_METERS;


//			mTmp.set(mCenter).rotate(angle).add(mBody.getPosition())
//					.sub(mHalfSize);

			mTmp.set(newCenter).rotate(angle).add(mBodyPos)
					.sub(mHalfSize);

			mSprite.setPosition(mTmp.x, mTmp.y);

			mSprite.setRotation(mRotation + angle);
			mSprite.draw(batch);
		} else {
			// else just draw it wherever it was defined at initialization
			mSprite.draw(batch);
		}
	}
}
