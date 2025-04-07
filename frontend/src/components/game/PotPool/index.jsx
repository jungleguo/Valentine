
import './PotPool.css'

export default function PotPool({ PotPools }) {
    const pools = Object.getOwnPropertyNames(PotPools)
        .map(prop => {
            let ele = PotPools[prop]
            return ele;
        });

    return (
        <div className="pots-container">
            { pools?.map(pool => {
                return  (<div key={pool?.id} className="pot-chip">
                    <div className="pot-amount">${pool?.pot}</div>
                    {pool?.id == 0 && <div className="main-pot-badge">Main</div>}
                </div>);
            })}
        </div>
    );
}